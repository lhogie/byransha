package byransha.web.view;

import byransha.web.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.nodes.system.Byransha.Function;
import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse.dialects;

public class CharExampleXY extends NodeEndpoint<BNode> implements TechnicalView {

	@Override
	public String whatItDoes() {
		return "CharExampleXY description";
	}
	public CharExampleXY(BBGraph g) {
		super(g);
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode n) {
		var r = new ObjectNode(null);

		{
			Function f = new Function();

			for (double x = 0; x < 50; ++x) {
				f.addXY(x, Math.cos(x));
			}

			r.set("cos(x)", f.toJson());
		}

		{
			Function f = new Function();

			for (double x = 0; x < 50; ++x) {
				f.addXY(x, Math.sin(x));
			}

			r.set("sin(x)", f.toJson());
		}

		return new EndpointJsonResponse(r, dialects.xy);
	}
}
