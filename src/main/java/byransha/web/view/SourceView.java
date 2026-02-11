package byransha.web.view;

import byransha.web.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.nodes.BNode;
import byransha.nodes.system.User;
import toools.src.Source;

public class SourceView extends NodeEndpoint<BNode> implements TechnicalView {

	@Override
	public String whatItDoes() {
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