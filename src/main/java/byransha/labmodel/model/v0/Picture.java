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

	public Picture(BBGraph db) {
		super(db);
	}

	public static class V extends NodeEndpoint<Picture> {

		public V(BBGraph db) {
			super(db);
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange,
				Picture node) {
			return new EndpointBinaryResponse("image/jpeg", node.get());
		}
	}

	@Override
	public void fromString(String s) {
	}
}
