package byransha.web.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.Byransha.Distribution;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointJsonResponse.dialects;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;

public class CharacterDistribution extends NodeEndpoint<BNode> implements View {

	@Override
	public String whatItDoes() {
		return "CharacterDistribution description";
	}

	public CharacterDistribution(BBGraph g) {
		super(g);
	}

	public CharacterDistribution(BBGraph g, int id) {
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
			var d = new Distribution<Integer>();
			n.getClass().getSimpleName().chars().forEach(d::addOccurence);
			r.set("class name", d.toJson());
		}

		{
			var d = new Distribution<Integer>();
			n.getClass().getPackageName().chars().forEach(d::addOccurence);
			r.set("package name", d.toJson());
		}

		return new EndpointJsonResponse(in, dialects.distribution);
	}
}
