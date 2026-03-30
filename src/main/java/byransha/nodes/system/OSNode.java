package byransha.nodes.system;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.nodes.primitive.TradUINodeView;

public class OSNode extends SystemNode {

	public OSNode(BGraph g) {
		super(g);
	}

	public void createViews() {
		cachedViews.elements.add(new View(g, this));
		super.createViews();
	}

	@Override
	public String whatIsThis() {
		return "information on the operating system";
	}

	@Override
	public String toString() {
		return ManagementFactory.getOperatingSystemMXBean().getName();
	}

	public static class View extends TradUINodeView<OSNode> {

		public View(BGraph g, OSNode os) {
			super(g, os);
		}

		@Override
		public ObjectNode describeAsJSON() {
			var r = new ObjectNode(factory);

			try {
				r.put("IP address", InetAddress.getLocalHost().getHostAddress());
				r.put("hostname", InetAddress.getLocalHost().getHostName());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			r.put("arch", ManagementFactory.getOperatingSystemMXBean().getArch());
			r.put("OS name", ManagementFactory.getOperatingSystemMXBean().getName());
			r.put("load average", ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
			r.put("#cores", Runtime.getRuntime().availableProcessors());
			r.put("%busy", ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()
					/ (double) Runtime.getRuntime().availableProcessors());
			return r;
		}

		@Override
		public String whatItShows() {
			return "OS details";
		}

		@Override
		protected boolean allowsEditing() {
			return false;
		}

		@Override
		public JComponent getComponent() {
			return getJSONDisplayComponent();
		}
	}

}
