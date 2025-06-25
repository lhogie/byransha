package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.ValuedNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class ListExistingNode<N extends BNode> extends NodeEndpoint<BNode> {

    @Override
    public String whatItDoes() {
        return "Lists existing nodes in the graph for a particuliar type.";
    }

    public ListExistingNode(BBGraph g) {
        super(g);
    }

    public ListExistingNode(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode currentNode) throws Throwable {
        var a = new ArrayNode(null);

        var nodeClass = graph.classes().stream()
                .filter(c -> c.getSimpleName().equals(in.get("type").asText()))
                .findFirst();

        if (nodeClass.isEmpty()) {
            return new EndpointJsonResponse(a, "Node type not found: " + in.get("type").asText());
        }

        graph.findAll(nodeClass.get(), node -> true)
                .forEach(node -> addNodeInfo(a, node));

        return new EndpointJsonResponse(a, "List_existing_node call executed successfully");
    }

    private void addNodeInfo(ArrayNode a, BNode node) {
        ObjectNode nodeInfo = new ObjectNode(null);
        nodeInfo.put("id", new IntNode(node.id()));
        nodeInfo.put("name", new TextNode(node.prettyName()));
        nodeInfo.put("type", new TextNode(node.getClass().getSimpleName()));
        if(node instanceof ValuedNode<?> vn) {
            nodeInfo.put("value", new TextNode(vn.getAsString()));
        }
        a.add(nodeInfo);
    }

}
