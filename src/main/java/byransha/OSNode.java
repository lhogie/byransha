package byransha;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;

public class OSNode extends SystemNode {

	@Override
	public String whatIsThis() {
		return "information on the operating system";
	}

	public OSNode(BBGraph db) {
		super(db);
		endOfConstructor();
	}

	@Override
	public String prettyName() {
		return ManagementFactory.getOperatingSystemMXBean().getName();
	}

	public static class View extends NodeEndpoint<BBGraph> implements TechnicalView {

		@Override
		public String whatItDoes() {
			return "shows OS details";
		}

		public View(BBGraph g) {
			super(g);
			endOfConstructor();
		}

		@Override
		public boolean sendContentByDefault() {
			return false;
		}

		@Override
		public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
				BBGraph node) throws Throwable {
			return new EndpointTextResponse("text/html", pw -> {
				pw.println("<ul>");
				try {
					String hostname = escapeHtml(InetAddress.getLocalHost().getHostName());
					pw.println("<li>IP address: " + hostname + "</li>");
				} catch (UnknownHostException e) {
					// Log the error properly instead of just printing stack trace
					System.err.println("Error getting hostname: " + e.getMessage());
					pw.println("<li>IP address: Unable to determine</li>");
				}
				String arch = escapeHtml(ManagementFactory.getOperatingSystemMXBean().getArch());
				String osName = escapeHtml(ManagementFactory.getOperatingSystemMXBean().getName());
				double loadAvg = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
				int cores = Runtime.getRuntime().availableProcessors();

				pw.println("<li>Arch: " + arch + "</li>");
				pw.println("<li>OS: " + osName + "</li>");
				pw.println("<li>Load avg: " + loadAvg + "</li>");
				pw.println("<li>#cores: " + cores + "</li>");
				pw.println("</ul>");
			});
		}

		// Helper method to escape HTML special characters
		private String escapeHtml(String input) {
			if (input == null) {
				return "";
			}
			return input.replace("&", "&amp;")
					.replace("<", "&lt;")
					.replace(">", "&gt;")
					.replace("\"", "&quot;")
					.replace("'", "&#39;");
		}
	}
}
