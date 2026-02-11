package byransha.web;

import byransha.nodes.BNode;
import byransha.BBGraph;
import byransha.nodes.system.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import toools.reflect.Clazz;

public abstract class NodeEndpoint<N extends BNode> extends Endpoint {

    public NodeEndpoint(BBGraph g) {
        super(g);
    }


    @Override
    public final EndpointResponse exec(
        ObjectNode input,
        User user,
        WebServer webServer,
        HttpsExchange exchange
    ) throws Throwable {
        try {
            N n = node(input.remove("node_id"), user);
            if (n == null) {
                return ErrorResponse.notFound(
                    "Required node not found or current node is null"
                );
            }
            return exec(input, user, webServer, exchange, n);
        } catch (NodeNotFoundException e) {
            return ErrorResponse.notFound(e.getMessage());
        }
    }

    private N node(JsonNode node, User user) throws NodeNotFoundException {
        if (node == null) {
            BNode currentNode = user.currentNode();
            if (currentNode != null) {
                // Validate that the current node still exists in the graph
                BNode validatedNode = g.findByID(currentNode.id());
                if (validatedNode == null) {
                    throw new NodeNotFoundException(
                        "Current node with ID " +
                        currentNode.id() +
                        " no longer exists in the graph."
                    );
                }
                return (N) validatedNode;
            }
            return null;
        }

        var s = node.asText();

        try {
            return (N) node(Integer.parseInt(s));
        } catch (NumberFormatException err) {
            Clazz.findClassOrFail(s);
            return null;
        }
    }

    public abstract EndpointResponse exec(
        ObjectNode input,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        N node
    ) throws Throwable;

    public BNode node(int id) throws NodeNotFoundException {
        BNode node = g.findByID(id);
        if (node == null) {
            throw new NodeNotFoundException(
                "Node with ID " + id + " does not exist in the graph."
            );
        }
        return node;
    }

    public List<BNode> nodes(int... ids) {
        return Arrays.stream(ids)
            .mapToObj(id -> {
                try {
                    return node(id);
                } catch (NodeNotFoundException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();
    }

    public enum TYPE {
        development,
        technical,
        business,
    }

    public TYPE type() {
        if (this instanceof DevelopmentView) {
            return TYPE.development;
        } else if (this instanceof TechnicalView) {
            return TYPE.technical;
        } else {
            return TYPE.business;
        }
    }

    // Custom exception for node not found scenarios
    private static class NodeNotFoundException extends Exception {

        public NodeNotFoundException(String message) {
            super(message);
        }
    }
}
