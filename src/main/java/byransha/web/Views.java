package byransha.web;

import byransha.nodes.BNode;
import byransha.BBGraph;
import byransha.nodes.system.User;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Views extends NodeEndpoint<BNode> implements TechnicalView {

    public Views(BBGraph g) {
        super(g);
    }


    @Override
    public String whatItDoes() {
        return "lists views";
    }

    @Override
    public EndpointJsonResponse exec(
        ObjectNode inputJson,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode currentNode
    ) {
        ArrayNode viewsNode = new ArrayNode(null);

        if (currentNode == null) {
            currentNode = g.root();
        }

        for (var e : g.endpointsUsableFrom(currentNode)) {
            if (e.canSee(user) && e.canExec(user)) {
                var ev = new ObjectNode(null);
                ev.set("pretty_name", new TextNode(e.prettyName()));
                ev.set("id", new TextNode("" + e.id()));
                ev.set("target", new TextNode(e.getTargetNodeType().getName()));
                ev.set("can read", new TextNode("" + e.canSee(user)));
                ev.set("can write", new TextNode("" + e.canSee(user)));
                ev.set("response_type", new TextNode(e.type().name()));

                // Lazy loading: Only execute views when explicitly requested to avoid performance issues
                boolean shouldExecute = false;
                if (e.getClass() != Views.class && e instanceof View v) {
                    // Check if this specific view should be executed
                    if (
                        inputJson.has("executeView") &&
                        inputJson.get("executeView").asText().equals(e.name())
                    ) {
                        shouldExecute = true;
                    }
                    // Or if it's a lightweight view that sends content by default and user requests all defaults
                    else if (
                        v.sendContentByDefault() &&
                        inputJson.has("executeDefaults") &&
                        inputJson.get("executeDefaults").asBoolean()
                    ) {
                        shouldExecute = true;
                    }
                }

                if (shouldExecute) {
                    try {
                        EndpointResponse result = e.exec(
                            inputJson.deepCopy(),
                            user,
                            webServer,
                            exchange,
                            user.currentNode()
                        );
                        ev.set("result", result.toJson());
                    } catch (SecurityException secEx) {
                        ev.set(
                            "error",
                            new TextNode(
                                "Execution blocked: " + secEx.getMessage()
                            )
                        );
                        ev.set(
                            "error_type",
                            new TextNode(
                                secEx
                                        .getMessage()
                                        .startsWith("Authentication required")
                                    ? "AuthenticationError"
                                    : "AuthorizationError"
                            )
                        );
                    } catch (Throwable err) {
                        err.printStackTrace();
                        var sw = new StringWriter();
                        err.printStackTrace(new PrintWriter(sw));
                        ev.set("error", new TextNode(sw.toString()));
                        ev.set("error_type", new TextNode("ExecutionError"));
                    }
                }

                viewsNode.add(ev);
            }
        }

        return new EndpointJsonResponse(viewsNode, this);
    }

    @Override
    public boolean sendContentByDefault() {
        return true;
    }
}
