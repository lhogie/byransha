package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClassInformation extends NodeEndpoint<BNode> {

    private static final ConcurrentMap<String, Class<?>> classCache =
        new ConcurrentHashMap<>();

    public ClassInformation(BBGraph g) {
        super(g);
        endOfConstructor();

    }

    @Override
    public String whatItDoes() {
        return "Send back the super and interface of the class.";
    }


    @Override
    public EndpointJsonResponse exec(
        ObjectNode in,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode node
    ) throws Throwable {
        var result = new ObjectNode(null);

        if (!in.has("classForm")) {
            return ErrorResponse.badRequest(
                "Missing required parameter: 'classForm'"
            );
        }

        String className = in.get("classForm").asText();
        if (className == null || className.isEmpty()) {
            return ErrorResponse.badRequest(
                "Invalid 'classForm' parameter: cannot be empty"
            );
        }

        try {
            Class<?> clazz = classCache.get(className);
            if (clazz == null) {
                clazz = Class.forName(className);
                classCache.putIfAbsent(className, clazz);
            }

            var current = clazz.getSuperclass();
            while (current != null) {
                result.set(
                    current.getSimpleName(),
                    new TextNode(current.getName())
                );
                current = current.getSuperclass();
            }

            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> i : interfaces) {
                result.set(i.getSimpleName(), new TextNode(i.getName()));
            }
            in.remove("classForm");

            return new EndpointJsonResponse(
                result,
                "Class super and interfaces"
            );
        } catch (ClassNotFoundException e) {
            return ErrorResponse.notFound("Class not found: " + className);
        } catch (Exception e) {
            return ErrorResponse.serverError(
                "Error retrieving class information: " + e.getMessage()
            );
        }
    }
}
