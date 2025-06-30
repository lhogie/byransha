package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

public class RemoveFromList extends NodeEndpoint<BNode> {

    public RemoveFromList(BBGraph g) {
        super(g);
    }

    public RemoveFromList(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange, BNode node) throws Throwable {
        var id = requireParm(input, "id").asInt();
        if(node instanceof ListNode) {
            ListNode<?> listNode = (ListNode<?>) node;
            if (id < 0 || id >= listNode.l.size()) {
                throw new IllegalArgumentException("Invalid index: " + id);
            }
            BNode toRemove = listNode.l.get(id);
            if (toRemove == null) {
                throw new IllegalArgumentException("No node found at index: " + id);
            }
            listNode.removeById(id);
            return new EndpointJsonResponse(NullNode.getInstance(), "Removed node with ID: " + toRemove.id() + " from list: " + listNode.prettyName());
        }
        else
            throw new IllegalArgumentException("Node is not a ListNode, cannot remove from it.");
    }

    @Override
    public String whatItDoes() {
        return "Removes a node from the graph.";
    }
}
