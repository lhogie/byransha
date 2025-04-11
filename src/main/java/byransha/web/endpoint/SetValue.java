package byransha.web.endpoint;

import byransha.*;
import byransha.web.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

public class SetValue extends NodeEndpoint<BNode> {

    @Override
    public String whatIsThis() {
        return "SetValue endpoint for modifying node values.";
    }

    public SetValue(BBGraph g) {
        super(g);
    }

    public SetValue(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode currentNode) throws Throwable {
        var targetID = requireParm(in, "target").asInt();
        var target = node(targetID);
        if(target == null){
            throw new Exception("no such node: " + targetID);
        }
        else if (target instanceof StringNode) {
            var value = requireParm(in, "value").asText();
            ((StringNode) target).set(value);
        }
        else if (target instanceof EmailNode){
            var value = requireParm(in, "value").asText();
            ((EmailNode) target).set(value);
        }
        else if (target instanceof DateNode) {
            var value = requireParm(in, "value").asText();
            ((DateNode) target).set(value);
        }

        return new NodeInfo(graph).exec(in, user, webServer, exchange, target);
    }
}
