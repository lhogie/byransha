package byransha.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.sun.net.httpserver.HttpExchange;

class HTTPResponse {
	final int code;
	final byte[] content;
	final String contentType;

	public HTTPResponse(int i, String contentType, byte[] content) {
		this.code = i;
		this.content = content;
		this.contentType = contentType;
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

                // Add security headers
                e.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
                e.getResponseHeaders().add("X-Frame-Options", "SAMEORIGIN");
                e.getResponseHeaders().add("X-XSS-Protection", "1; mode=block");
                e.getResponseHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
                e.getResponseHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

                // Handle preflight OPTIONS request
                if (e.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    e.getResponseHeaders().set("Content-Type", "text/plain");
                    e.sendResponseHeaders(204, -1); // No content
                    return;
                }

                // Set content type
                e.getResponseHeaders().set("Content-type", contentType);

                // Add cache control headers for static resources
                if (isStaticResource(contentType)) {
                    // Cache static resources for 1 day (86400 seconds)
                    e.getResponseHeaders().set("Cache-Control", "public, max-age=86400");

                    // Add ETag header for cache validation
                    String eTag = "\"" + Integer.toHexString(java.util.Arrays.hashCode(content)) + "\"";
                    e.getResponseHeaders().set("ETag", eTag);

                    // Check if client sent If-None-Match header
                    String ifNoneMatch = e.getRequestHeaders().getFirst("If-None-Match");
                    if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
                        // Resource not modified, return 304
                        e.sendResponseHeaders(304, -1);
                        return;
                    }
                } else {
                    // Don't cache dynamic content
                    e.getResponseHeaders().set("Cache-Control", "no-store, must-revalidate");
                    e.getResponseHeaders().set("Pragma", "no-cache");
                }

                // Check if client accepts gzip encoding and if content is compressible
                String acceptEncoding = e.getRequestHeaders().getFirst("Accept-Encoding");
                boolean useGzip = acceptEncoding != null && 
                                 acceptEncoding.contains("gzip") && 
                                 isCompressibleContentType(contentType) && 
                                 content.length > 1024; // Only compress content larger than 1KB

                byte[] responseContent = content;

                // Apply gzip compression if appropriate
                if (useGzip) {
                    try {
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(content.length);
                        try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
                            gzipStream.write(content);
                        }
                        responseContent = byteStream.toByteArray();
                        e.getResponseHeaders().set("Content-Encoding", "gzip");
                    } catch (IOException ex) {
                        // If compression fails, fall back to uncompressed content
                        System.err.println("Gzip compression failed: " + ex.getMessage());
                        responseContent = content;
                    }
                }

                // Send response
                e.sendResponseHeaders(code, responseContent.length);
                output.write(responseContent);
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
