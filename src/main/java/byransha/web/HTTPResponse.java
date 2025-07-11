package byransha.web;

import com.sun.net.httpserver.HttpExchange;
import com.upokecenter.cbor.CBORObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

class HTTPResponse {

    final int code;
    final byte[] content;
    final String contentType;

    private Long rangeStart;
    private Long rangeEnd;
    private Long contentLength;

    public HTTPResponse(int i, String contentType, byte[] content) {
        this.code = i;
        this.content = content;
        this.contentType = contentType;
        this.contentLength = (long) content.length;
    }

    /**
     * Constructor for range requests
     *
     * @param code HTTP status code
     * @param contentType Content type of the response
     * @param content Full content of the response
     * @param rangeStart Start byte of the range (inclusive)
     * @param rangeEnd End byte of the range (inclusive)
     */
    public HTTPResponse(
        int code,
        String contentType,
        byte[] content,
        long rangeStart,
        long rangeEnd
    ) {
        this.code = code;
        this.content = content;
        this.contentType = contentType;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.contentLength = (long) content.length;
    }

    void send(HttpExchange e) throws IOException {
        try (var output = e.getResponseBody()) {
            try {
                String origin = e.getRequestHeaders().getFirst("Origin");
                if (origin == null || origin.isEmpty()) {
                    origin = "http://localhost:5173";
                }
                e
                    .getResponseHeaders()
                    .add("Access-Control-Allow-Origin", origin);
                e
                    .getResponseHeaders()
                    .add("Access-Control-Allow-Credentials", "true");
                e
                    .getResponseHeaders()
                    .add(
                        "Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS"
                    );
                e
                    .getResponseHeaders()
                    .add(
                        "Access-Control-Allow-Headers",
                        "Content-Type, Authorization"
                    );
                e.getResponseHeaders().add("Access-Control-Max-Age", "3600");

                e.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
                e.getResponseHeaders().add("X-Frame-Options", "SAMEORIGIN");
                e.getResponseHeaders().add("X-XSS-Protection", "1; mode=block");
                e
                    .getResponseHeaders()
                    .add(
                        "Referrer-Policy",
                        "strict-origin-when-cross-origin"
                    );
                e
                    .getResponseHeaders()
                    .add(
                        "Strict-Transport-Security",
                        "max-age=31536000; includeSubDomains"
                    );

                if (e.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    e.getResponseHeaders().set("Content-Type", "text/plain");
                    e.sendResponseHeaders(204, -1);
                    return;
                }

                String accept = e.getRequestHeaders().getFirst("Accept");
                boolean supportsCbor =
                    accept != null && accept.contains("application/cbor");

                byte[] responseData = content;
                String finalContentType = contentType;

                if (supportsCbor && contentType.contains("application/json")) {
                    var cborObject = CBORObject.FromJSONBytes(content);
                    responseData = cborObject.EncodeToBytes();
                    finalContentType = "application/cbor";
                }

                e.getResponseHeaders().set("Content-type", finalContentType);

                if (isStaticResource(finalContentType)) {
                    e
                        .getResponseHeaders()
                        .set("Cache-Control", "public, max-age=86400");

                    String eTag =
                        """ +
                        Integer.toHexString(
                            java.util.Arrays.hashCode(responseData)
                        ) +
                        """;
                    e.getResponseHeaders().set("ETag", eTag);

                    String ifNoneMatch = e
                        .getRequestHeaders()
                        .getFirst("If-None-Match");
                    if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
                        e.sendResponseHeaders(304, -1);
                        return;
                    }
                } else {
                    e
                        .getResponseHeaders()
                        .set("Cache-Control", "no-store, must-revalidate");
                    e.getResponseHeaders().set("Pragma", "no-cache");
                }

                String acceptEncoding = e
                    .getRequestHeaders()
                    .getFirst("Accept-Encoding");
                boolean useGzip =
                    acceptEncoding != null &&
                    acceptEncoding.contains("gzip") &&
                    isCompressibleContentType(finalContentType) &&
                    responseData.length > 1024 &&
                    rangeStart == null;

                String connection = e
                    .getRequestHeaders()
                    .getFirst("Connection");
                boolean keepAlive =
                    connection != null &&
                    connection.equalsIgnoreCase("keep-alive");

                if (keepAlive) {
                    e.getResponseHeaders().set("Connection", "keep-alive");
                    e
                        .getResponseHeaders()
                        .set("Keep-Alive", "timeout=5, max=1000");
                } else {
                    e.getResponseHeaders().set("Connection", "close");
                }

                if (useGzip) {
                    e.getResponseHeaders().set("Content-Encoding", "gzip");
                    e.getResponseHeaders().add("Vary", "Accept-Encoding");
                    e.sendResponseHeaders(code, 0);
                    try (
                        GZIPOutputStream gzipStream = new GZIPOutputStream(
                            output,
                            8192
                        )
                    ) {
                        gzipStream.write(responseData);
                    }
                } else {
                    if (rangeStart != null && rangeEnd != null) {
                        e.getResponseHeaders().set("Accept-Ranges", "bytes");
                        e
                            .getResponseHeaders()
                            .set(
                                "Content-Range",
                                String.format(
                                    "bytes %d-%d/%d",
                                    rangeStart,
                                    rangeEnd,
                                    contentLength
                                )
                            );

                        int rangeLength = (int) (rangeEnd - rangeStart + 1);
                        e.sendResponseHeaders(206, rangeLength);
                        output.write(
                            responseData,
                            rangeStart.intValue(),
                            rangeLength
                        );
                    } else {
                        e.getResponseHeaders().set("Accept-Ranges", "bytes");
                        e.sendResponseHeaders(code, responseData.length);
                        output.write(responseData);
                    }
                }

                output.flush();
            } catch (IOException ex) {
                System.err.println(
                    "Error sending response: " + ex.getMessage()
                );
                throw ex;
            }
        } catch (IOException ex) {
            System.err.println(
                "Error closing output stream: " + ex.getMessage()
            );
        }
        // System.out.println("sent: " + code + " content:" + new String(content));
    }

    /**
     * Determines if the content type is compressible.
     * @param contentType The content type to check
     * @return true if the content type is compressible, false otherwise
     */
    private boolean isCompressibleContentType(String contentType) {
        if (contentType == null) {
            return false;
        }

        String lowerContentType = contentType.toLowerCase();
        return (
            lowerContentType.contains("text/") ||
            lowerContentType.contains("application/json") ||
            lowerContentType.contains("application/javascript") ||
            lowerContentType.contains("application/cbor") ||
            lowerContentType.contains("application/xml") ||
            lowerContentType.contains("image/svg+xml")
        );
    }

    /**
     * Determines if the content type represents a static resource that can be cached.
     * @param contentType The content type to check
     * @return true if the content type is a static resource, false otherwise
     */
    private boolean isStaticResource(String contentType) {
        if (contentType == null) {
            return false;
        }

        String lowerContentType = contentType.toLowerCase();
        return (
            lowerContentType.contains("image/") ||
            lowerContentType.contains("text/css") ||
            lowerContentType.contains("application/javascript") ||
            lowerContentType.contains("font/") ||
            lowerContentType.contains("application/font") ||
            lowerContentType.endsWith(".woff") ||
            lowerContentType.endsWith(".woff2") ||
            lowerContentType.endsWith(".ttf")
        );
    }
}
