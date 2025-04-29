package byransha.web.view;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.DevelopmentView;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

final public class AllViews extends NodeEndpoint<BNode> implements DevelopmentView {

	@Override
	public String whatIsThis() {
		return "Description for AllViews";
	}

	public AllViews(BBGraph db) {
		super(db);
	}

	public AllViews(BBGraph db, int id) {
		super(db, id);
	}

	static final List<String> imgFormats = List.of("svg", "png", "jpeg", "jpg");

	@Override
	public boolean sendContentByDefault() {
		return false;
	}

	@Override
	public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode n)
			throws Throwable {
		return new EndpointTextResponse("text/html", pw -> {
			pw.println("<ul>");

			for (var v : graph.endpointsUsableFrom(n)) {
				if (v == this)
					continue;

				pw.println("<li><h3>" + v.name() + "</h3>");
				pw.println("<ul>");
				pw.println("<li>");
				try {
					EndpointResponse r = v.exec(in, user, webServer, exchange, n);

					if (r instanceof EndpointTextResponse) {
						pw.println((String) r.data);
					} else if (imgFormats.contains(r.contentType)) {
						pw.println("<img src=\"data:image/png;base64," + Arrays.toString(Base64.getEncoder().encode((byte[]) r.data))
								+ "\" />");
					} else if (r instanceof EndpointJsonResponse) {
						pw.println("<script>obj = " + r.data()
								+ "; document.write(\"<pre>\" + JSON.stringify(obj) + \"</pre>\");</script>");
					} else {
						pw.println("Raw data: " + new String((byte[]) r.data));
					}
				} catch (Throwable err) {
					pw.println("Error: " + err.getMessage());
				}

				pw.println("</ul>");
			}

			pw.println("</ul>");
		});
	}

}