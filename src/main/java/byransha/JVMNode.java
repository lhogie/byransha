package byransha;

import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;
import java.lang.management.ManagementFactory;
import toools.text.TextUtilities;

public class JVMNode extends SystemNode {

    public JVMNode(BBGraph g) {
        super(g);
    }

    @Override
    public String prettyName() {
        return "Java Virtual Machine";
    }

    @Override
    public String whatIsThis() {
        return "represents the server's JVM";
    }

    public static class View
        extends NodeEndpoint<BBGraph>
        implements TechnicalView {

        public View(BBGraph g) {
            super(g);
        }



        @Override
        public String whatItDoes() {
            return "describes the server's JVM";
        }

        @Override
        public boolean sendContentByDefault() {
            return false;
        }

        @Override
        public EndpointTextResponse exec(
            ObjectNode input,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            BBGraph node
        ) throws Throwable {
            return new EndpointTextResponse("text/html", doc -> {
                doc.println("<ul>");
                doc.println(
                    "<li>Heap size: " +
                    TextUtilities.toHumanString(
                        ManagementFactory.getMemoryMXBean()
                            .getHeapMemoryUsage()
                            .getUsed()
                    ) +
                    "B"
                );
                doc.println("</ul><hr>System properties:<ul>");

                for (var e : System.getProperties().entrySet()) {
                    doc.println("<li>" + e.getKey() + "=" + e.getValue());
                }

                doc.println("</ul>");
            });
        }
    }

    public static class Kill extends NodeEndpoint<JVMNode> implements Changer {

        public Kill(BBGraph db) {
            super(db);
        }


        @Override
        public boolean canExec(User user) {
            return user.name.get().equals("admin");
        }

        @Override
        public String whatItDoes() {
            return "kills the JVM";
        }

        @Override
        public EndpointResponse exec(
            ObjectNode in,
            User user,
            WebServer webServer,
            HttpsExchange http,
            JVMNode jvm
        ) throws Throwable {
            new Thread(() -> {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.exit(0);
            })
                .start();

            return new EndpointTextResponse("text/plain", doc ->
                doc.print("exiting...")
            );
        }
    }
}
