package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.PrimitiveValueNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;
import toools.gui.Utilities;

public class ClassAttributeField extends NodeEndpoint<BNode> implements View {

	public ClassAttributeField(BBGraph g) {
		super(g);
	}

	@Override
	public boolean sendContentByDefault() {
		return false;
	}

	@Override
	public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange, BNode node)
			throws Throwable {

		ObjectNode r = new ObjectNode(null);

		r.put("id", node.id());
		r.put("class", node.getClass().getName());
		r.put("color", Utilities.toRGBHex(getColor()));
		r.put("prettyName", node.prettyName());
		r.put("whatIsThis", node.whatIsThis());
		r.put("canSee", node.canSee(user));
		r.put("canEdit", node.canEdit(user));
		
		if(node instanceof PrimitiveValueNode pvn) {
			r.put("value", pvn.getAsString());
		}
		
		var errsNode = new ArrayNode(null);
		node.errors(0).forEach(err -> errsNode.add(new TextNode(err.error)));
		r.set("errors", errsNode);
		

		ObjectNode outsNode = new ObjectNode(null);
		r.set("outs", outsNode);
		
		node.forEachOut((s, o) -> {
			ObjectNode ro = new ObjectNode(null);
			outsNode.set(s, ro);
		});
		
		return new EndpointJsonResponse(r, ClassAttributeField.class.getName());
	}

	@Override
	public String whatItDoes() {
		return null;
	}
}
