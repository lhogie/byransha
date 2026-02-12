package byransha.web.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.Byransha;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;
import toools.Stop;

public class OutDegreeDistribution extends NodeEndpoint<BNode> implements TechnicalView {

	@Override
	public String whatItDoes() {
		return "shows distribution for out nodes";
	}

	public OutDegreeDistribution(BBGraph db) {
		super(db);
	}

	@Override
	public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
			throws Throwable {
		var d = new Byransha.Distribution<Integer>();
		BBGraph g = (node instanceof BBGraph) ? (BBGraph) node : node.g;

		g.forEachNode(n -> {
			d.addOccurence(n.computeOuts().size());
			return Stop.no;
		});

		return new EndpointJsonResponse(d.toJson(), EndpointJsonResponse.dialects.distribution);
	}

	@Override
	public boolean sendContentByDefault() {
		return false;
	}
}
