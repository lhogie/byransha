package byransha.web.endpoint;
import byransha.web.View;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;


import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.ValuedNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;


public class ShowOut extends NodeEndpoint<BNode> implements View {

    @Override
    public String whatIsThis() {
        return "Endpoint to show every values of the current node";
    }

    public ShowOut(BBGraph g) {
        super(g);
    }

    public ShowOut(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode currentNode) throws Throwable {

        var a = new ArrayNode(null);
        var b = new ObjectNode(null);
        b.set("number_of_outs",new IntNode(currentNode.outs().size()));
        a.add(b);

        for(String key : currentNode.outs().keySet()) {
            b = new ObjectNode(null);
            BNode node = currentNode.outs().get(key);
            b.set("id", new IntNode(node.id()));
            b.set("name",new TextNode(key));
            b.set("type", new TextNode(node.getClass().getSimpleName()));
            b.set("value", new TextNode(node.toString())) ;
            a.add(b);
        }


        //System.out.println("id de currentNode:"+ currentNode.id());
        return new EndpointJsonResponse(a,"response for edit");
    }

    @Override
    public boolean sendContentByDefault() {
        return true;
    }
}
