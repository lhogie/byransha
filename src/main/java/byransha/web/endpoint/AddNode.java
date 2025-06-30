package byransha.web.endpoint;

import byransha.*;
import byransha.labmodel.model.v0.Position;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class AddNode<N extends BNode> extends NodeEndpoint<BNode> {

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
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode currentNode) throws Throwable {
        var a = new ObjectNode(null);
        var className = requireParm(in, "BNodeClass").asText();
        System.out.println("Adding node of class: " + className);

        Class<N> clazz = (Class<N>) Class.forName(className);
        var node = BNode.create(graph, clazz);
        if(node != null) {
            a.put("id", new IntNode(node.id()));
            a.put("name", new TextNode(node.prettyName()));
            a.put("type", new TextNode(node.getClass().getSimpleName()));
            if(node instanceof ValuedNode<?> vn) {
                a.put("value", new TextNode(vn.getAsString()));
            }
            a.put("class", new TextNode(className));
            a.put("message", new TextNode("Node created successfully"));

            if(currentNode instanceof ListNode<?> listNode) {
                @SuppressWarnings("unchecked")
                ListNode<N> typedListNode = (ListNode<N>) listNode;
                typedListNode.add(node);
            }

            if(currentNode instanceof SetNode<?> setNode) {
                @SuppressWarnings("unchecked")
                SetNode<N> typedSetNode = (SetNode<N>) setNode;
                typedSetNode.add(node);
            }

        } else {
            return ErrorResponse.serverError("Failed to create node of class: " + className);
        }

        return new EndpointJsonResponse(a,"Add_node call executed successfully");
    }

}
