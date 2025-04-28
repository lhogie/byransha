package byransha.web;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

class HTTPResponse {
	int code;
	byte[] content;
	String contentType;

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

                // Handle preflight OPTIONS request
                if (e.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    e.getResponseHeaders().set("Content-Type", "text/plain");
                    e.sendResponseHeaders(204, -1); // No content
                    return;
                }

                // Set content type and send response
                e.getResponseHeaders().set("Content-type", contentType);
                e.sendResponseHeaders(code, content.length);
                output.write(content);
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
}
