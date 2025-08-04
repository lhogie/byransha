package byransha.web.endpoint;

import byransha.*;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AddNode<N extends BNode> extends NodeEndpoint<BNode> {

    private static final ConcurrentMap<
        String,
        Class<? extends BNode>
    > classCache = new ConcurrentHashMap<>();

    @Override
    public String whatItDoes() {
        return "Adds a new node to the graph.";
    }

    public AddNode(BBGraph g) {
        super(g);
    }

    public AddNode(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(
        ObjectNode in,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode currentNode
    ) throws Throwable {
        var a = new ObjectNode(null);
        var className = requireParm(in, "BNodeClass").asText();

        Class<N> clazz;
        Class<?> cachedClass = classCache.get(className);
        if (cachedClass != null) {
            clazz = (Class<N>) cachedClass;
        } else {
            clazz = (Class<N>) Class.forName(className);
            classCache.putIfAbsent(className, clazz);
        }
        var node = graph.create( clazz);
        if (node != null) {
            a.set("id", new IntNode(node.id()));
            a.set("name", new TextNode(node.prettyName()));
            a.set("type", new TextNode(node.getClass().getSimpleName()));
            if (node instanceof ValuedNode<?> vn) {
                a.set("value", new TextNode(vn.getAsString()));
            }
            a.set("class", new TextNode(className));
            a.set("message", new TextNode("Node created successfully"));

            if (currentNode instanceof ListNode<?> listNode) {
                @SuppressWarnings("unchecked")
                ListNode<N> typedListNode = (ListNode<N>) listNode;
                typedListNode.add(node);
            }
        } else {
            return ErrorResponse.serverError(
                "Failed to create node of class: " + className
            );
        }

        return new EndpointJsonResponse(
            a,
            "Add_node call executed successfully"
        );
    }
}
