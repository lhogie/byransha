package byransha.web.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.DevelopmentView;
import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import toools.src.Source;

public class SourceView extends NodeEndpoint<BNode> implements DevelopmentView {

	@Override
	public String whatIsThis() {
		return "SourceView description";
	}

	public SourceView(BBGraph db) {
		super(db);
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}

	@Override
	public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange, BNode node)
			throws Throwable {
		return new EndpointTextResponse("text/java", pw -> {
			pw.print(Source.getClassSourceCode(node.getClass()));
		});
	}

}