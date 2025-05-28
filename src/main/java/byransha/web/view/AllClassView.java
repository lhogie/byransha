package byransha.web.view;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import java.util.ArrayList;

public class AllClassView extends NodeEndpoint<BNode>  implements View {

    @Override
    public String whatItDoes() {
        return "AllClassView description";
    }

    public AllClassView(BBGraph g) {
        super(g);
    }

    public AllClassView(int id) {
        super(null, id);
    }

    @Override
    public boolean sendContentByDefault() {
        return true;
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode n) {
        var nameArray = new ArrayList<String>();
        nameArray.add("Person");
        nameArray.add("Lab");
        nameArray.add("I3S");
        nameArray.add("ACMClassifier");
        nameArray.add("Building");
        nameArray.add("Campus");
        nameArray.add("CNRS");
        nameArray.add("Contract");
        nameArray.add("Country");
        nameArray.add("CR");
        nameArray.add("Device");
        nameArray.add("DR");


        var nodeArray = new ArrayNode(null);

        for (String name : nameArray) {
            var node = new ObjectNode(null);
            node.put("name", new TextNode(name));
            nodeArray.add(node);
        }

        return new EndpointJsonResponse(nodeArray, "Array node of all class names");
    }
}
