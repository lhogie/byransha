package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
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
        var a = new ObjectNode(null);
        if(in.has("classForm")) {
            var clazz = Class.forName(in.get("classForm").asText());
            Field[] field = clazz.getDeclaredFields();
            for (Field f: field){
                a.put(f.getName(), new TextNode(f.getType().getName()));
            }
        }

        return new EndpointJsonResponse(a, "Add_node call");
    }

}
