package byransha.web.view;

import byransha.web.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;
import toools.extern.Proces;

public abstract class ModelJSONDOTView extends NodeEndpoint<BBGraph> implements TechnicalView {
	public ModelJSONDOTView(BBGraph db) {
		super(db);
	}

	protected static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BBGraph n)
			throws Throwable {
		String dialect = requireParm(in, "dialect").asText();
		var dot = new ModelDOTView(g).exec(in, user, webServer, exchange, n).data.getBytes();
		var stdout = Proces.exec("dot", dot, "-T" + dialect);
		return new EndpointJsonResponse(mapper.readTree(new String(stdout)), dialect);
	}

}
