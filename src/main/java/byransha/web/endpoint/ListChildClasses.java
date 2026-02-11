package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.nodes.BNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import java.util.ArrayList;
import java.util.List;

public class ListChildClasses extends NodeEndpoint<BNode> {

    public ListChildClasses(BBGraph g) {
        super(g);
        endOfConstructor();
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode currentNode) throws Throwable {
        var a = new ArrayNode(null);

        var classeName = requireParm(in, "className").asText();

        List<String> classes = new ArrayList<>();

        g.forEachNode(node -> {
            if (isChildOfClass(node.getClass(), classeName)) {
                if(!classes.contains(node.getClass().getSimpleName())) classes.add(node.getClass().getSimpleName());
            }
        });
        a.addAll(classes.stream().map(TextNode::new).toList());
        return new EndpointJsonResponse(a, "classeName");
    }

    private boolean isChildOfClass(Class<?> nodeClass, String targetClassName) {
        Class<?> currentClass = nodeClass.getSuperclass();

        while (currentClass != null && !currentClass.equals(BNode.class) && !currentClass.equals(Object.class)) {
            if (currentClass.getSimpleName().equalsIgnoreCase(targetClassName)) {
                return true;
            }
            currentClass = currentClass.getSuperclass();
        }

        if (currentClass != null && currentClass.equals(BNode.class) &&
            currentClass.getSimpleName().equalsIgnoreCase(targetClassName)) {
            return true;
        }

        return false;
    }

    @Override
    public String whatItDoes() {
        return "Return a list of child classes of a given node type.";
    }
}
