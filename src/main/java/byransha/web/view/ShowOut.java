package byransha.web.view;
import byransha.*;
import byransha.web.View;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;


public class ShowOut extends NodeEndpoint<BNode> implements View {

    @Override
    public String whatItDoes() {
        return "Endpoint to show every values of the current node";
    }

    public ShowOut(BBGraph g) {
        super(g);
    }

    public ShowOut(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode n)
            throws Throwable {
        var a = new ArrayNode(null);

        n.forEachOut((name, out) -> {
            if (!out.canSee(user) || out.id() == 0) {
                return;
            }
            var b = new ObjectNode(null);
            b.set("id", new IntNode(out.id()));
            b.set("name", new TextNode(name));
            b.set("type", new TextNode(out.getClass().getSimpleName()));
            if(out.canEdit(user)){b.set("editable",new TextNode("true"));}else{b.set("editable",new TextNode("false"));}
            if (out instanceof ValuedNode<?> vn) {
                b.set("value", new TextNode(vn.getAsString()));
                b.set("mimeType", new TextNode(vn.getMimeType()));
            }
            a.add(b);
        });

        //System.out.println("id de currentNode:"+ currentNode.id());
        return new EndpointJsonResponse(a, n.prettyName()+'@'+n.id());
    }

    @Override
    public boolean sendContentByDefault() {
        return true;
    }
}