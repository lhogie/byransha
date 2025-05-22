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

public class AddNode extends NodeEndpoint<BNode> {

    @Override
    public String whatItDoes() {
        return "Adds a new node to the graph.";
    }

    public AddNode(BBGraph g) {
        super(g);
    }

    public AddNode(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
            throws Throwable {
        var a = new ObjectNode(null);
        a.put("message", new TextNode("Node added successfully"));
        System.out.println("Adding node in Add node endpoint in: " + in);
        System.out.println("Adding node in Add node endpoint user: " + user);
        System.out.println("Adding node in Add node endpoint WebServer: " + webServer);
        System.out.println("Adding node in Add node endpoint exchange: " + exchange);
        System.out.println("Adding node in Add node endpoint node: " + node.getClass());

        if(!in.isEmpty()){
            System.err.println("Adding node in Add node endpoint in: +++++++++++++++++++++++++   " + in.get("test"));
        }

        return new EndpointJsonResponse(    a, "Node added successfully");
    }

}
