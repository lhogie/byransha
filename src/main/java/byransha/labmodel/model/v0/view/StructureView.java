package byransha.labmodel.model.v0.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.User;
import byransha.labmodel.model.v0.Structure;
import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

final public class StructureView extends NodeEndpoint<Structure> {

	@Override
	public String getDescription() {
		return "StructureView for managing structure-related endpoints.";
	}

	public StructureView(BBGraph g) {
		super(g);
	}

	public StructureView(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
			Structure s) throws Throwable {
		return new EndpointTextResponse("text/html", pw -> {
			pw.println("<ul>");
			pw.println("<li>Director: " + s.director.etatCivil.name.get());
			pw.println("<li>" + s.members.size() + " members");
			pw.println("<li>#offices: " + s.offices.size());
			pw.println("<li>Office surface: " + s.totalSurface());
			pw.println("<li>avg surface/user: " + s.occupationRatio());
			pw.println("<li>occupationRatio: " + s.occupationRatio());
			pw.println("</ul>");
		});
	}

}