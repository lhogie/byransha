package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.ValuedNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;
import toools.text.TextUtilities;

import java.util.Comparator;

public class ListExistingNode extends NodeEndpoint<BNode> {

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
            return ErrorResponse.notFound("Node type not found: " + in.get("type").asText());
        }

        String query = in.has("query") ? in.get("query").asText().toLowerCase() : null;

        var filteredNodes = graph.findAll(nodeClass.get(), node -> {
            if (query == null || query.isEmpty()) return true;
            String name = node.prettyName();
            return name != null && name.toLowerCase().contains(query);
        });

        if (query != null && !query.isEmpty()) {
            filteredNodes.sort(Comparator.comparingInt(node -> {
                String name = node.prettyName();
                if (name == null) return Integer.MAX_VALUE;
                return TextUtilities.computeLevenshteinDistance(name.toLowerCase(), query);
            }));
        }

        filteredNodes.forEach(node -> addNodeInfo(a, node));

        return new EndpointJsonResponse(a, "List_existing_node call executed successfully");
    }


    private void addNodeInfo(ArrayNode a, BNode node) {
        ObjectNode nodeInfo = new ObjectNode(null);
        nodeInfo.set("id", new IntNode(node.id()));
        nodeInfo.set("name", new TextNode(node.prettyName()));
        nodeInfo.set("type", new TextNode(node.getClass().getSimpleName()));
        if(node instanceof ValuedNode<?> vn) {
            nodeInfo.set("value", new TextNode(vn.getAsString()));
        }
        a.add(nodeInfo);
    }

}
