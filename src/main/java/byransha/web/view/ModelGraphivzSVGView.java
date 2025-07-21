package byransha.web.view;

import byransha.BBGraph;
import byransha.User;
import byransha.web.EndpointBinaryResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;
import java.nio.charset.StandardCharsets;
import toools.extern.Proces;

public class ModelGraphivzSVGView
    extends NodeEndpoint<BBGraph>
    implements TechnicalView {

    public ModelGraphivzSVGView(BBGraph db) {
        super(db);
    }

    public ModelGraphivzSVGView(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public boolean sendContentByDefault() {
        return false;
    }

    @Override
    public String whatItDoes() {
        return "graphical (graphviz-based) representations of the model in the graph";
    }

    @Override
    public EndpointBinaryResponse exec(
        ObjectNode in,
        User u,
        WebServer webServer,
        HttpsExchange exchange,
        BBGraph db
    ) throws Throwable {
        var dot = graph
            .findEndpoint(ModelDOTView.class)
            .exec(in, u, webServer, exchange, db)
            .data;
        var generatorNode = in.remove("generator");
        var generator = generatorNode == null ? null : generatorNode.asText();
        var svg = gen(dot, generator);
        return new EndpointBinaryResponse("image/svg+xml", svg);
    }

    public static byte[] gen(String dot, String generator) {
        if (generator == null || generator.equals("dot")) {
            return Proces.exec(
                "dot",
                dot.getBytes(StandardCharsets.UTF_8),
                "-Tsvg"
            );
        } else if (generator.equals("fdp")) {
            return Proces.exec(
                "fdp",
                dot.getBytes(StandardCharsets.UTF_8),
                "-Tsvg",
                "-Gmaxiter=10000",
                "-GK=1"
            );
        } else {
            throw new IllegalArgumentException(
                "unknown generator: " + generator
            );
        }
    }
}
