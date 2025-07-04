package byransha.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.sun.net.httpserver.HttpExchange;

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
	public HTTPResponse(int code, String contentType, byte[] content, long rangeStart, long rangeEnd) {
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
                e.getResponseHeaders().add("Access-Control-Allow-Origin", origin);
                e.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
                e.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                e.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                e.getResponseHeaders().add("Access-Control-Max-Age", "3600");

                e.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
                e.getResponseHeaders().add("X-Frame-Options", "SAMEORIGIN");
                e.getResponseHeaders().add("X-XSS-Protection", "1; mode=block");
                e.getResponseHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
                e.getResponseHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

                if (e.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    e.getResponseHeaders().set("Content-Type", "text/plain");
                    e.sendResponseHeaders(204, -1);
                    return;
                }

                e.getResponseHeaders().set("Content-type", contentType);

                if (isStaticResource(contentType)) {
                    e.getResponseHeaders().set("Cache-Control", "public, max-age=86400");

                    String eTag = "\"" + Integer.toHexString(java.util.Arrays.hashCode(content)) + "\"";
                    e.getResponseHeaders().set("ETag", eTag);

                    String ifNoneMatch = e.getRequestHeaders().getFirst("If-None-Match");
                    if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
                        e.sendResponseHeaders(304, -1);
                        return;
                    }
                } else {
                    e.getResponseHeaders().set("Cache-Control", "no-store, must-revalidate");
                    e.getResponseHeaders().set("Pragma", "no-cache");
                }

                String acceptEncoding = e.getRequestHeaders().getFirst("Accept-Encoding");
                boolean useGzip = acceptEncoding != null && 
                                 acceptEncoding.contains("gzip") && 
                                 isCompressibleContentType(contentType) && 
                                 content.length > 1024;

                byte[] responseContent = content;

                if (useGzip) {
                    try {
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(content.length);

                      try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream, 16384) {
                            {
                                def.setLevel(6);
                            }
                        }) {
                            gzipStream.write(content);
                        }

                        responseContent = byteStream.toByteArray();
                        e.getResponseHeaders().set("Content-Encoding", "gzip");

                        e.getResponseHeaders().add("Vary", "Accept-Encoding");
                    } catch (IOException ex) {
                        System.err.println("Gzip compression failed: " + ex.getMessage());
                        responseContent = content;
                    }
                }

                String connection = e.getRequestHeaders().getFirst("Connection");
                boolean keepAlive = connection != null && connection.equalsIgnoreCase("keep-alive");

                if (keepAlive) {
                    e.getResponseHeaders().set("Connection", "keep-alive");
                    e.getResponseHeaders().set("Keep-Alive", "timeout=5, max=1000");
                } else {
                    e.getResponseHeaders().set("Connection", "close");
                }

                if (rangeStart != null && rangeEnd != null) {
                    e.getResponseHeaders().set("Accept-Ranges", "bytes");
                    e.getResponseHeaders().set("Content-Range", 
                            String.format("bytes %d-%d/%d", rangeStart, rangeEnd, contentLength));

                    int rangeLength = (int) (rangeEnd - rangeStart + 1);
                    e.sendResponseHeaders(206, rangeLength);

                    int bufferSize = 8192;
                    byte[] buffer = new byte[bufferSize];
                    long remaining = rangeLength;
                    int offset = rangeStart.intValue();

                    while (remaining > 0) {
                        int bytesToRead = (int) Math.min(bufferSize, remaining);
                        System.arraycopy(responseContent, offset, buffer, 0, bytesToRead);
                        output.write(buffer, 0, bytesToRead);
                        offset += bytesToRead;
                        remaining -= bytesToRead;
                    }
                } else {
                    e.getResponseHeaders().set("Accept-Ranges", "bytes");

                    e.sendResponseHeaders(code, responseContent.length);

                    int bufferSize = 8192;
                    byte[] buffer = new byte[bufferSize];
                    int offset = 0;

                    while (offset < responseContent.length) {
                        int bytesToRead = Math.min(bufferSize, responseContent.length - offset);
                        System.arraycopy(responseContent, offset, buffer, 0, bytesToRead);
                        output.write(buffer, 0, bytesToRead);
                        offset += bytesToRead;
                    }
                }

                output.flush();
            } catch (IOException ex) {
                System.err.println("Error sending response: " + ex.getMessage());
                throw ex;
            }
        } catch (IOException ex) {
            System.err.println("Error closing output stream: " + ex.getMessage());
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
        return lowerContentType.contains("text/") || 
               lowerContentType.contains("application/json") || 
               lowerContentType.contains("application/javascript") || 
               lowerContentType.contains("application/xml") ||
               lowerContentType.contains("image/svg+xml");
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
        return lowerContentType.contains("image/") || 
               lowerContentType.contains("text/css") || 
               lowerContentType.contains("application/javascript") ||
               lowerContentType.contains("font/") ||
               lowerContentType.contains("application/font") ||
               lowerContentType.endsWith(".woff") ||
               lowerContentType.endsWith(".woff2") ||
               lowerContentType.endsWith(".ttf");
    }
}
