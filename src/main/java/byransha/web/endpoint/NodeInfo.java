package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class NodeInfo extends NodeEndpoint<BNode> {

    public NodeInfo(BBGraph db) {
        super(db);
    }

    public NodeInfo(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public String whatItDoes() {
        return "info about a node";
    }

    @Override
    public EndpointJsonResponse exec(
        ObjectNode inputJson,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode node
    ) {
        if (!node.canSee(user)) {
            return ErrorResponse.forbidden(
                "User does not have permission to view node " + node.id()
            );
        }

        var r = new ObjectNode(null);
        r.set("id", new TextNode("" + node.id()));
        r.set("pretty_name", new TextNode(node.prettyName()));
        r.set("class", new TextNode(node.getClass().getName()));
        r.set("to_string", new TextNode(node.toString()));
        r.set("can read", new TextNode("" + node.canSee(user)));
        r.set("can write", new TextNode("" + node.canSee(user)));
        r.set("out", new IntNode(node.outDegree()));
        r.set("in", new IntNode(node.ins().size()));

        var availableEndpoints = new ArrayNode(null);

        for (var e : graph.endpointsUsableFrom(node)) {
            if (e.canSee(user)) {
                if (e.canExec(user)) {
                    availableEndpoints.add(new TextNode(e.name()));
                }
            }
        }

        r.set("views", availableEndpoints);
        return new EndpointJsonResponse(r, this);
    }
}
