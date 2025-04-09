package byransha.labmodel.model.v0;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.User;
import byransha.ValuedNode;
import byransha.web.NodeEndpoint;
import byransha.web.EndpointBinaryResponse;
import byransha.web.EndpointResponse;
import byransha.web.WebServer;

public class Picture extends ValuedNode<byte[]> {

	@Override
	public String getDescription() {
		return "Picture node containing image data";
	}

	public Picture(BBGraph db) {
		super(db);
	}

	public static class V extends NodeEndpoint<Picture> {

		public V(BBGraph db) {
			super(db);
		}

		public V(BBGraph db, int id) {
			super(db, id);
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange,
				Picture node) {
			return new EndpointBinaryResponse("image/jpeg", node.get());
		}

		@Override
		public String getDescription() {
			return "Endpoint for Picture nodes";
		}
	}

	@Override
	public void fromString(String s) {
	}
}
