package byransha.nodes.lab.model.v0.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.nodes.system.User;
import byransha.nodes.lab.model.v0.Lab;
import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

final public class LabView extends NodeEndpoint<Lab> {

	@Override
	public String whatItDoes() {
		return "show infos about a lab";
	}

	public LabView(BBGraph db) {
		super(db);
		endOfConstructor();
	}


	@Override
	public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange, Lab lab)
			throws Throwable {
		return new EndpointTextResponse("text/html", pw -> {
			pw.println("<ul>");
			pw.println("<li>HFDS: " + lab.HFDS.etatCivil.name.get());
			pw.println("</ul>");
		});
	}
}