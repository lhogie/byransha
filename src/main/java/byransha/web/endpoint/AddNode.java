package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
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

        Class<N> clazz = (Class<N>) Class.forName(className);
        var node = BNode.create(graph, clazz);
        if(node != null) {
            a.put("id", new IntNode(node.id()));
            a.put("class", new TextNode(className));
            a.put("message", new TextNode("Node created successfully"));
        } else {
            return new EndpointJsonResponse(null, "Failed to create node of class: " + className);
        }
        return new EndpointJsonResponse(a,"Add_node call executed successfully");
    }

}
