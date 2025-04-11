package byransha.web.endpoint;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.ValuedNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;


public class Edit extends NodeEndpoint<BNode> {

    @Override
    public String whatIsThis() {
        return "Edit endpoint for BNode";
    }

    public Edit(BBGraph g) {
        super(g);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode currentNode) throws Throwable {

        if (currentNode instanceof ValuedNode<?>) {
            System.out.println("valeur editable");
        }else{
            System.out.println("valeur non editable");
        }


        System.out.println("Valeur de currentNode:"+ currentNode.outDegree());

        var a = new ArrayNode(null);
        var b = new ObjectNode(null);
        b.set("Test",new IntNode(currentNode.id()));
        a.add(b);
        return new EndpointJsonResponse(a, this);
    }
}
