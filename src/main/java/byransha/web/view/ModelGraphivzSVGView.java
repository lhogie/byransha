package byransha.web.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.User;
import byransha.web.EndpointBinaryResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;
import toools.extern.Proces;

public class ModelGraphivzSVGView extends NodeEndpoint<BBGraph> implements TechnicalView {

	public ModelGraphivzSVGView(BBGraph db) {
		super(db);
	}

	@Override
	public EndpointBinaryResponse exec(ObjectNode in, User u, WebServer webServer, HttpsExchange exchange, BBGraph db)
			throws Throwable {
		var dot = new ModelDOTView(graph).exec(in, u, webServer, exchange, db);
		var stdout = Proces.exec("dot", dot.data.getBytes(), "-Tsvg");
		return new EndpointBinaryResponse("image/svg", stdout);
//		return Proces.exec("fdp", dot, "-Tsvg", "-Gmaxiter=10000", "-GK=1");
	}
}
