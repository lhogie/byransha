package byransha.web.endpoint;

import byransha.*;
import byransha.annotations.ListOptions;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class AddExistingNode<N extends BNode> extends NodeEndpoint<BNode> {

    @Override
    public String whatItDoes() {
        return "Adds an existing node to the graph.";
    }

    public AddExistingNode(BBGraph g) {
        super(g);
    }

    public AddExistingNode(BBGraph g, int id) {
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

        if (
            currentNode instanceof ListNode<?> listNode &&
            listNode.getElementType() == ListOptions.ElementType.STRING
        ) {
            String idToLink = requireParm(in, "id").asText();
            a.put("id of the list to link", new IntNode(currentNode.id()));
            a.put("string of the thing we link", new TextNode(idToLink));
            if (idToLink == null || idToLink.isEmpty()) {
                return ErrorResponse.badRequest("ID cannot be null or empty.");
            }

            listNode.select(idToLink);

            return new EndpointJsonResponse(
                a,
                "Add_existing_node call executed successfully"
            );
        } else {
            int idToLink = requireParm(in, "id").asInt();
            a.put("id of the list to link", new IntNode(currentNode.id()));
            a.put("id of the thing we link", new IntNode(idToLink));

            var existingNode = graph.findByID(idToLink);
            if (existingNode == null) {
                return ErrorResponse.notFound(
                    "Node with ID " + idToLink + " does not exist in the graph."
                );
            } else {
                a.put("id", new IntNode(existingNode.id()));
                a.put("name", new TextNode(existingNode.prettyName()));

                if (currentNode instanceof ListNode<?> listNode) {
                    @SuppressWarnings("unchecked")
                    ListNode<N> typedListNode = (ListNode<N>) listNode;
                    typedListNode.add((N) existingNode);
                }
            }

            return new EndpointJsonResponse(
                a,
                "Add_existing_node call executed successfully"
            );
        }
    }
}
