package byransha.web.endpoint;

import byransha.*;
import byransha.labmodel.model.v0.BusinessNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.sun.net.httpserver.HttpsExchange;
import toools.text.TextUtilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SearchNode<N extends BNode> extends NodeEndpoint<BNode> {

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
        String query;
        HashMap<String, String> options = new HashMap<>();
        if (currentNode instanceof SearchForm && in.isEmpty()) {
            currentNode.forEachOut((name, outNode) -> {
                if(outNode instanceof RadioNode<?> rn ) {
                    if(rn.getSelectedOption() != null) options.put(name, rn.getSelectedOption().toString());
                } else if (outNode instanceof ValuedNode vn) {
                    options.put(name, vn.getAsString());
                }
            });
            query = options.get("searchTerm");
            options.remove("searchTerm");

            System.out.println(options);
            ((SearchForm) currentNode).results.removeAll();
        }
        else query = requireParm(in, "query").asText();  in.remove("query");
        if (query == null || query.isEmpty()) {
            return ErrorResponse.badRequest("Query parameter is missing or empty.");
        }

        var nodes = graph.findAll(BusinessNode.class, node -> {
            boolean baseCondition = !node.deleted
                    && !node.getClass().getSimpleName().equals("SearchForm")
                    &&  node.prettyName().toLowerCase().contains(query.toLowerCase());

            if(!options.isEmpty() && baseCondition){
                if(!(options.get("searchClass").equals("null") || options.get("searchClass").equals("") || options.get("searchClass") == null)
                        && !node.getClass().getSimpleName().toLowerCase().contains(options.get("searchClass").toLowerCase())) return false;
            }

            return baseCondition;
        });
        nodes.sort(Comparator.comparingInt(node -> {
            String name = node.prettyName();
            if (name == null) return Integer.MAX_VALUE;
            return TextUtilities.computeLevenshteinDistance(name.toLowerCase(), query);
        }));

        nodes.forEach(node -> {
            if(currentNode instanceof SearchForm) ((SearchForm) currentNode).results.add(node);
            addNodeInfo(a, node);
        });

        return new EndpointJsonResponse(a, "Search results for: " + query);
    }

    private void addNodeInfo(ArrayNode a, BNode node) {
        ObjectNode nodeInfo = new ObjectNode(null);
        nodeInfo.set("id", new IntNode(node.id()));
        nodeInfo.set("name", new TextNode(node.prettyName()));
        nodeInfo.set("type", new TextNode(node.getClass().getSimpleName()));
        nodeInfo.set("isValid", BooleanNode.valueOf(node.isValid()));

        node.forEachOut((name, outNode) -> {
            if (outNode instanceof ImageNode imageNode) {
                nodeInfo.set("img", new TextNode(imageNode.getAsString()));
                nodeInfo.set("imgMimeType", new TextNode(imageNode.getMimeType()));
            }
        });

        a.add(nodeInfo);
    }

}
