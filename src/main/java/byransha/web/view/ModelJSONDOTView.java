package byransha.web.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.User;
import byransha.web.DevelopmentView;
import byransha.web.NodeEndpoint;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.WebServer;
import toools.extern.Proces;

public abstract class ModelJSONDOTView extends NodeEndpoint<BBGraph> implements DevelopmentView {
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
