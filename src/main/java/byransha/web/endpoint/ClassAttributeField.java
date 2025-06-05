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

import java.lang.reflect.Field;

public class ClassAttributeField extends NodeEndpoint<BNode> {

    @Override
    public String whatItDoes() {
        return "Adds a new node to the graph.";
    }

    public ClassAttributeField(BBGraph g) {
        super(g);
    }

    public ClassAttributeField(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
            throws Throwable {
        var a = new ArrayNode(null);
        node.forEachOut((name, out) -> {
            var b = new ObjectNode(null);
            b.set("id", new IntNode(out.id()));
            b.set("name", new TextNode(name));
            b.set("type", new TextNode(out.getClass().getSimpleName()));
            if(out instanceof ValuedNode<?> vn) {
                b.set("value", new TextNode(vn.getAsString()));
            }
            a.add(b);
        });


        return new EndpointJsonResponse(a, "Add_node call");
    }

}
