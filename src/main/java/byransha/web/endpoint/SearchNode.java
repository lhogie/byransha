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
import java.util.*;
import toools.text.TextUtilities;

public class SearchNode<N extends BNode> extends NodeEndpoint<BNode> {

    @Override
    public String whatItDoes() {
        return "Searches for existing nodes in the graph by name or type, with pagination support.";
    }

    public SearchNode(BBGraph g) {
        super(g);
    }

    public SearchNode(BBGraph g, int id) {
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
        ArrayNode dataArray = new ArrayNode(null);
        String query;
        HashMap<String, String> options = new HashMap<>();

        // Handle SearchForm inputs
        if (currentNode instanceof SearchForm && in.isEmpty()) {
            currentNode.forEachOut((name, outNode) -> {
                if (outNode instanceof RadioNode<?> rn) {
                    if (rn.getSelectedOption() != null) {
                        options.put(name, rn.getSelectedOption().toString());
                    }
                } else if (outNode instanceof ValuedNode vn) {
                    options.put(name, vn.getAsString());
                }
            });
            query = options.get("searchTerm");
            options.remove("searchTerm");

            ((SearchForm) currentNode).results.removeAll();
        } else {
            query = requireParm(in, "query").asText();
        }

        // Pagination parameters
        int page     = in.has("page")     ? in.get("page").asInt()     : 1;
        int pageSize = in.has("pageSize") ? in.get("pageSize").asInt() : 50;

        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 50;

        in.remove("query");
        in.remove("page");
        in.remove("pageSize");

        if (query == null || query.isEmpty()) {
            return ErrorResponse.badRequest("Query parameter is missing or empty.");
        }

        // Search matching nodes
        var nodes = graph.findAll(BusinessNode.class, node -> {
            boolean baseCondition =
                    !node.deleted &&
                            !node.getClass().getSimpleName().equals("SearchForm") &&
                            node.prettyName().toLowerCase().contains(query.toLowerCase());

//            if (!options.isEmpty() && baseCondition) {
//                String classOption = options.get("searchClass");
//                if (
//                        classOption != null &&
//                                !classOption.equalsIgnoreCase("null") &&
//                                !classOption.equals("") &&
//                                !node.getClass().getSimpleName().toLowerCase().contains(classOption.toLowerCase())
//                ) return false;
//            }

            return baseCondition;
        });

        // Sort by Levenshtein distance
        nodes.sort(Comparator.comparingInt(node -> {
            String name = node.prettyName();
            if (name == null) return Integer.MAX_VALUE;
            return TextUtilities.computeLevenshteinDistance(name.toLowerCase(), query);
        }));

        int total = nodes.size();
        int fromIndex = Math.min((page - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<BusinessNode> pageNodes = nodes.subList(fromIndex, toIndex);

        pageNodes.forEach(node -> {
            if (currentNode instanceof SearchForm sf) {
                sf.results.add(node);
            }
            addNodeInfo(dataArray, node);
        });

        // Wrap in metadata
        ObjectNode response = new ObjectNode(null);
        response.set("data", dataArray);
        response.put("page", new IntNode(page));
        response.put("pageSize", new IntNode(pageSize));
        response.put("total", new IntNode(total));
        response.put("hasNext", BooleanNode.valueOf(toIndex < total));

        return new EndpointJsonResponse(response, "Search results for: " + query);
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
