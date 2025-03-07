package byransha.web.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BNode;
import byransha.Byransha.Distribution;
import byransha.BBGraph;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointJsonResponse.dialects;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class CharacterDistribution extends NodeEndpoint<BNode> {

	public CharacterDistribution(BBGraph g) {
		super(g);
		sendContentByDefault = true;
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode n) {
		var r = new ObjectNode(null);

		{
			var d = new Distribution<Integer>();
			n.getClass().getSimpleName().chars().forEach(c -> d.addOccurence(c));
			r.set("class name", d.toJson());
		}

		{
			var d = new Distribution<Integer>();
			n.getClass().getPackageName().chars().forEach(c -> d.addOccurence(c));
			r.set("package name", d.toJson());
		}

		return new EndpointJsonResponse(in, dialects.distribution);
	}
}
