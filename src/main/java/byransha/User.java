package byransha;

import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class User extends BNode {
    public StringNode name;
    public StringNode passwordNode;
    public final Deque<BNode> stack = new ConcurrentLinkedDeque<>();

    public User(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        stack.push(g.application.rootNode);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        name = new StringNode(g, creator, InstantiationInfo.persisting);
        passwordNode = new StringNode(g, creator, InstantiationInfo.persisting);
        setColor("#032cfc", this);
    }

    public User(BBGraph g, User creator, InstantiationInfo ii, String user, String password) {
        this(g, creator, ii);
        name.set(user, this);
        passwordNode.set(password, this);
    }

    @Override
    public String whatIsThis() {
        return "a user of the system";
    }

    public BNode currentNode() {
        return stack.isEmpty() ? null : stack.getLast();
    }

    @Override
    public boolean canSee(User user) {
        return user == this;
    }

    @Override
    public boolean canEdit(User user) {
        return user == this;
    }

    public boolean accept(String username, String p) {
        return name.get().equals(username) && passwordNode.get().equals(p);
    }

    public static class UserView
        extends NodeEndpoint<User>
        implements TechnicalView {

        public UserView(BBGraph g) {
            super(g);
            endOfConstructor();
        }

        @Override
        public String whatItDoes() {
            return "show some things about users";
        }

        @Override
        public boolean sendContentByDefault() {
            return false;
        }

        @Override
        public EndpointResponse exec(
            ObjectNode input,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            User node
        ) throws Throwable {
            return new EndpointTextResponse("text/html", pw -> {
                pw.println("<ul>");
                pw.print("<li>Navigation history: ");
                //				user.stack.forEach(n -> pw.print(linkTo(n, "X")));
                pw.println("<li>admin? " + false);
                pw.println("</ul>");
            });
        }
    }

    public static class History
        extends NodeEndpoint<BNode>
        implements TechnicalView {

        public History(BBGraph g) {
            super(g);
            endOfConstructor();
        }

        @Override
        public String whatItDoes() {
            return "gets the navigation history";
        }

        @Override
        public boolean sendContentByDefault() {
            return true;
        }

        @Override
        public EndpointResponse exec(
            ObjectNode input,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            BNode node
        ) throws Throwable {
            var a = new ArrayNode(null);
            user.stack.forEach(e -> a.add(e.toJSONNode()));
            return new EndpointJsonResponse(a, this);
        }
    }

    @Override
    public String prettyName() {
        if(name == null || name.get() == null ) {return null;}
        return name.get();
    }

    public boolean isAdmin() {
        return name.get().equals("admin");
    }

    public void setAdmin(boolean admin) {
        if (admin) {
            name.set("admin", this);
        } else {
            if (name.get().equals("admin")) {
                name.set("user", this);
            }
        }
    }
}
