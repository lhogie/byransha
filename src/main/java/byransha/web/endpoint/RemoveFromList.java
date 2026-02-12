package byransha.web.endpoint;

import byransha.nodes.system.User;
import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ListNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

public class RemoveFromList<N extends BNode> extends NodeEndpoint<BNode> {

    private static final NullNode NULL_NODE = NullNode.getInstance();

    public RemoveFromList(BBGraph g) {
        super(g);
    }

    @Override
    public EndpointJsonResponse exec(
        ObjectNode input,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode node
    ) throws Throwable {
        int nodeId = requireParm(input, "id").asInt();
        var nodeToRemove = g.findByID(nodeId);

        if (nodeToRemove == null) {
            StringBuilder errorMsg = new StringBuilder("Node with ID ");
            errorMsg.append(nodeId).append(" not found in the graph.");
            return ErrorResponse.notFound(errorMsg.toString());
        }

        if (node instanceof ListNode<?> listNode) {
            @SuppressWarnings("unchecked")
            ListNode<N> typedListNode = (ListNode<N>) listNode;
            typedListNode.remove((N) nodeToRemove);

            StringBuilder successMsg = new StringBuilder(
                "Removed node with ID: "
            );
            successMsg
                .append(nodeToRemove.id())
                .append(" from list: ")
                .append(listNode.prettyName());

            return new EndpointJsonResponse(NULL_NODE, successMsg.toString());
        } else {
            return ErrorResponse.badRequest(
                "Node is not a ListNode or SetNode, cannot remove from it."
            );
        }
    }

    @Override
    public String whatItDoes() {
        return "Removes a node from the graph.";
    }
}
