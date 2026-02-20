package byransha.graph;

import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.nodes.primitive.ValuedNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;
import byransha.web.EndpointJsonResponse.dialects;
import graph.AnyGraph;
import graph.BVertex;

public class InOutsNivoView extends NodeEndpoint<BNode> implements TechnicalView {

	public InOutsNivoView(BBGraph db) {
		super(db);
	}

	@Override
	public String whatItDoes() {
		return "generates a NIVO description of the graph";
	}

	@Override
	public boolean sendContentByDefault() {
		return false;
	}

	@Override
	public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode n) {
		var g = new AnyGraph();
		var currentVertex = g.ensureHasVertex(n);
		setVertexProperties(currentVertex, n, "pink");
		currentVertex.size = 20;
		var limit = 99;
		AtomicInteger currentNumberNodes = new AtomicInteger(0);

		if (n.getClass() != BBGraph.class) {
			n.forEachOut((role, outNode) -> {
				if (currentNumberNodes.get() <= limit || outNode.getClass() == BBGraph.class) {
					var outVertex = g.ensureHasVertex(outNode);
					setVertexProperties(outVertex, outNode, "blue");
					var arc = g.newArc(currentVertex, outVertex);
					arc.label = role;
					arc.color = "red";
					currentNumberNodes.getAndIncrement();
				}
			});

			n.computeIns().forEach(inLink -> {
				if (inLink.source().canSee(user) && !(inLink.source() instanceof ValuedNode<?>)) {
					var inVertex = g.ensureHasVertex(inLink.source());
					setVertexProperties(inVertex, inLink.source(), "pink");
					var arc = g.newArc(inVertex, currentVertex);
					arc.style = "dotted";
					arc.label = inLink.role();
				}
			});
		}

		return new EndpointJsonResponse(g.toNivoJSON(), dialects.nivoNetwork);
	}

	private void setVertexProperties(BVertex vertex, BNode node, String defaultColor) {
		vertex.color = node.getColor().toString();
		vertex.label = node.prettyName();
		vertex.whatIsThis = node.whatIsThis();
		vertex.className = node.getClass().getName();
	}
}