package byransha.web.endpoint;

import byransha.*;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

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

    private Field findField(Class<?> clazz, String name) {
        try {
            return clazz.getField(name);
        } catch (NoSuchFieldException e) {
            try {
                Field f = clazz.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ex) {
                return null;
            }
        }
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

            System.out.println("Processing out node: " + out.getClass().getSimpleName() + " with name: " + name);
            if(out instanceof ListNode<?> listNode){
                try{
                    Field field = findField(node.getClass(), name);
                    var genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType parameterizedType) {
                        var actualType = parameterizedType.getActualTypeArguments()[0];
                        b.set("listNodeType", new TextNode(actualType.getTypeName()));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            a.add(b);
        });

        return new EndpointJsonResponse(a, "Add_node call");
    }

}
