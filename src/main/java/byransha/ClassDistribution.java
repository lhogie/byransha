package byransha;

import byransha.web.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

public class ClassDistribution
        extends NodeEndpoint<BNode>
        implements View {

    public ClassDistribution(BBGraph db) {
        super(db);
    }

    @Override
    public String whatItDoes() {
        return "shows distributed for out nodes";
    }

    @Override
    public EndpointResponse exec(
            ObjectNode in,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            BNode node
    ) throws Throwable {
        var d = new Byransha.Distribution<String>();
        BBGraph g = (node instanceof BBGraph) ? (BBGraph) node : node.graph;
        g.forEachNode(n -> d.addOccurence(n.getClass().getName()));
        return new EndpointJsonResponse(d.toJson(), EndpointJsonResponse.dialects.distribution);
    }

    @Override
    public boolean sendContentByDefault() {
        return false;
    }
}
