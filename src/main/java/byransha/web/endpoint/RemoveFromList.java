package byransha.web.endpoint;

import byransha.*;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

public class RemoveFromList<N extends BNode> extends NodeEndpoint<BNode> {

    public RemoveFromList(BBGraph g) {
        super(g);
    }

    public RemoveFromList(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange, BNode node) throws Throwable {
        int nodeId = requireParm(input, "id").asInt();
        var nodeToRemove = graph.findByID(nodeId);

        if (nodeToRemove == null) {
            return ErrorResponse.notFound("Node with ID " + nodeId + " not found in the graph.");
        }

        if (node instanceof ListNode<?> listNode) {
            @SuppressWarnings("unchecked")
            ListNode<N> typedListNode = (ListNode<N>) listNode;
            typedListNode.remove((N) nodeToRemove);
            return new EndpointJsonResponse(NullNode.getInstance(), "Removed node with ID: " + nodeToRemove.id() + " from list: " + listNode.prettyName());
        }
        else if (node instanceof SetNode<?> setNode) {
            @SuppressWarnings("unchecked")
            SetNode<N> typedSetNode = (SetNode<N>) setNode;
            typedSetNode.remove((N) nodeToRemove);
            return new EndpointJsonResponse(NullNode.getInstance(), "Removed node with ID: " + nodeToRemove.id() + " from set: " + setNode.prettyName());
        }
        else {
            return ErrorResponse.badRequest("Node is not a ListNode or SetNode, cannot remove from it.");
        }
    }

    @Override
    public String whatItDoes() {
        return "Removes a node from the graph.";
    }
}
