package byransha.web.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.Byransha.Function;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointJsonResponse.dialects;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;

public class CharExampleXY extends NodeEndpoint<BNode> implements View {

	@Override
	public String whatItDoes() {
		return "CharExampleXY description";
	}
	public CharExampleXY(BBGraph g) {
		super(g);
	}

	public CharExampleXY(BBGraph g, int id) {
		super(g, id);
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
