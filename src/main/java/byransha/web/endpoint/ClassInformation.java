package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import java.lang.reflect.Field;

public class ClassInformation extends NodeEndpoint<BNode> {

    @Override
    public String whatItDoes() {
        return "Send back the super and interface of the class.";
    }

    public ClassInformation(BBGraph g) {
        super(g);
    }

    public ClassInformation(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
            throws Throwable {
        var result = new ObjectNode(null);

        if (in.has("classForm")) {
            var clazz = Class.forName(in.get("classForm").asText());

            var current = clazz.getSuperclass();
            while (current != null) {
                result.put(current.getSimpleName(), new TextNode(current.getName()));
                current = current.getSuperclass();
            }

            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> i : interfaces) {
                result.put(i.getSimpleName(), new TextNode(i.getName()));
            }
            in.remove("classForm");
        }

        return new EndpointJsonResponse(result, "Class super and interfaces");
    }


}
