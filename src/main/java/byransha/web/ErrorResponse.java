package byransha.web;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Utility class for creating error responses with appropriate status codes.
 */
public class ErrorResponse {
    
    /**
     * Creates a JSON response for a bad request (400) error.
     * 
     * @param message The error message
     * @return An EndpointJsonResponse with a 400 status code
     */
    public static EndpointJsonResponse badRequest(String message) {
        ObjectNode errorNode = new ObjectNode(null);
        errorNode.set("error", new TextNode(message));
        errorNode.set("error_type", new TextNode("BadRequest"));
        return new EndpointJsonResponse(errorNode, "error", 400);
    }
    
    /**
     * Creates a JSON response for an authentication (401) error.
     * 
     * @param message The error message
     * @return An EndpointJsonResponse with a 401 status code
     */
    public static EndpointJsonResponse unauthorized(String message) {
        ObjectNode errorNode = new ObjectNode(null);
        errorNode.set("error", new TextNode(message));
        errorNode.set("error_type", new TextNode("AuthenticationError"));
        return new EndpointJsonResponse(errorNode, "error", 401);
    }
    
    /**
     * Creates a JSON response for an authorization (403) error.
     * 
     * @param message The error message
     * @return An EndpointJsonResponse with a 403 status code
     */
    public static EndpointJsonResponse forbidden(String message) {
        ObjectNode errorNode = new ObjectNode(null);
        errorNode.set("error", new TextNode(message));
        errorNode.set("error_type", new TextNode("AuthorizationError"));
        return new EndpointJsonResponse(errorNode, "error", 403);
    }
    
    /**
     * Creates a JSON response for a not found (404) error.
     * 
     * @param message The error message
     * @return An EndpointJsonResponse with a 404 status code
     */
    public static EndpointJsonResponse notFound(String message) {
        ObjectNode errorNode = new ObjectNode(null);
        errorNode.set("error", new TextNode(message));
        errorNode.set("error_type", new TextNode("NotFoundError"));
        return new EndpointJsonResponse(errorNode, "error", 404);
    }
    
    /**
     * Creates a JSON response for a server (500) error.
     * 
     * @param message The error message
     * @return An EndpointJsonResponse with a 500 status code
     */
    public static EndpointJsonResponse serverError(String message) {
        ObjectNode errorNode = new ObjectNode(null);
        errorNode.set("error", new TextNode(message));
        errorNode.set("error_type", new TextNode("ServerError"));
        return new EndpointJsonResponse(errorNode, "error", 500);
    }
    
    /**
     * Creates a JSON response for a server (500) error with a stack trace.
     * 
     * @param throwable The throwable that caused the error
     * @return An EndpointJsonResponse with a 500 status code
     */
    public static EndpointJsonResponse serverError(Throwable throwable) {
        ObjectNode errorNode = new ObjectNode(null);
        errorNode.set("error", new TextNode(throwable.getMessage()));
        errorNode.set("error_type", new TextNode("ServerError"));
        
        // Add stack trace
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        errorNode.set("stack_trace", new TextNode(stackTrace.toString()));
        
        return new EndpointJsonResponse(errorNode, "error", 500);
    }
    
    /**
     * Creates a text response for an error with the specified status code.
     * 
     * @param message The error message
     * @param statusCode The HTTP status code
     * @return An EndpointTextResponse with the specified status code
     */
    public static EndpointTextResponse textError(String message, int statusCode) {
        return new EndpointTextResponse("text/plain", message, statusCode);
    }
}