package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.nodes.BNode;
import byransha.nodes.system.User;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.PrimitiveValueNode;
import byransha.nodes.primitive.StringNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class SetValue extends NodeEndpoint<BNode> {

    public SetValue(BBGraph g) {
        super(g);
    }

    @Override
    public String whatItDoes() {
        return "modifies the value of valued nodes";
    }


    @Override
    public EndpointJsonResponse exec(
        ObjectNode in,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode target
    ) throws Throwable {
        var a = new ObjectNode(null);
        if (in.isEmpty()) {
            return ErrorResponse.badRequest(
                "Request body is empty. Expected 'id' and 'value' parameters."
            );
        }
        if (!in.has("id")) {
            return ErrorResponse.badRequest("Missing required parameter: 'id'");
        }
        if (!in.has("value")) {
            return ErrorResponse.badRequest(
                "Missing required parameter: 'value'"
            );
        }
        int id;
        try {
            id = in.get("id").asInt();
        } catch (Exception e) {
            return ErrorResponse.badRequest(
                "Invalid 'id' parameter: must be an integer"
            );
        }

        var node = g.findByID(id);
        if (node == null) {
            return ErrorResponse.notFound(
                "Node with ID " + id + " not found in the graph."
            );
        }

        a.set("id", new IntNode(node.id()));
        a.set("name", new TextNode(node.prettyName()));
        a.set("type", new TextNode(node.getClass().getSimpleName()));
        a.set("isValid", BooleanNode.valueOf(node.isValid()));

        var value = in.get("value");

        try {
            if(node instanceof PrimitiveValueNode<?> pv) {
                pv.fromString(value.asText(), user);
                a.set("value", new TextNode(value.asText()));
            }
            else if (node instanceof ListNode lc) {
                if (!lc.allowMultiple()) {
                    if (value.isArray() && value.size() > 1) {
                        return ErrorResponse.badRequest(
                            "ListNode " +
                            lc.prettyName() +
                            " does not allow multiple values."
                        );
                    }
                    lc.select(value.asInt(), user);
                    a.set("value", value);
                } else {
                    if (!value.isArray()) {
                        return ErrorResponse.badRequest(
                            "Expected an array for ListNode " +
                            lc.prettyName() +
                            "."
                        );
                    }
                    for (JsonNode item : value) {
                        lc.add(new StringNode(g, user, InstantiationInfo.persisting, item.asText()), user);
                    }
                    a.set("value", value);
                }
            } else {
                return ErrorResponse.badRequest(
                    "Node type " +
                    node.getClass().getSimpleName() +
                    " is not supported for value setting."
                );
            }
        } catch (Exception e) {
            return ErrorResponse.serverError(
                "Error setting value: " + e.getMessage()
            );
        }

        in.removeAll();
        return new EndpointJsonResponse(a, "Setting the value");
    }
}
