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

import java.util.Comparator;
import java.util.List;

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
                return levenshteinDistance(name.toLowerCase(), query);
            }));
        }

        filteredNodes.forEach(node -> addNodeInfo(a, node));

        return new EndpointJsonResponse(a, "List_existing_node call executed successfully");
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost
                    );
                }
            }
        }

        return dp[a.length()][b.length()];
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
