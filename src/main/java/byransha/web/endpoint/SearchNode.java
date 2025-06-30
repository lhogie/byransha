package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.labmodel.model.v0.BusinessNode;
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

public class SearchNode extends NodeEndpoint<BNode> {

    @Override
    public String whatItDoes() {
        return "Searches for existing nodes in the graph by name or type.";
    }

    public SearchNode(BBGraph g) {
        super(g);
    }

    public SearchNode(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode currentNode) throws Throwable {
        var a = new ArrayNode(null);

        var query = requireParm(in, "query").asText();

        if (query == null || query.isEmpty()) {
            return ErrorResponse.badRequest("Query parameter is missing or empty.");
        }

        var nodes = graph.findAll(BusinessNode.class, node -> node.prettyName().toLowerCase().contains(query.toLowerCase()));

        nodes.sort(Comparator.comparingInt(node -> {
            String name = node.prettyName();
            if (name == null) return Integer.MAX_VALUE;
            return TextUtilities.computeLevenshteinDistance(name.toLowerCase(), query);
        }));

        nodes.forEach(node -> {
            addNodeInfo(a, node);
        });

        return new EndpointJsonResponse(a, "Search results for: " + query);
    }

    private void addNodeInfo(ArrayNode a, BNode node) {
        ObjectNode nodeInfo = new ObjectNode(null);
        nodeInfo.put("id", new IntNode(node.id()));
        nodeInfo.put("name", new TextNode(node.prettyName()));
        nodeInfo.put("type", new TextNode(node.getClass().getSimpleName()));
        a.add(nodeInfo);
    }

}
