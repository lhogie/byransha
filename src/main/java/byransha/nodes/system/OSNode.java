package byransha.nodes.system;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import butils.ByUtils;
import byransha.graph.BBGraph;
import byransha.graph.view.NodeView;

public class OSNode extends SystemB {

	public OSNode(BBGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "information on the operating system";
	}

	@Override
	public String prettyName() {
		return ManagementFactory.getOperatingSystemMXBean().getName();
	}

	public static class View extends NodeView<JVMNode> {

		public View(BBGraph g, JVMNode jvm) {
			super(g, jvm);
		}

		@Override
		public JsonNode toJSON(JVMNode jvm) {
			var r = new ObjectNode(factory);
			r.put("heap size", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());

			try {
				r.put("IP address", InetAddress.getLocalHost().getHostName());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			r.put("arch", ManagementFactory.getOperatingSystemMXBean().getArch());
			r.put("OS name", ManagementFactory.getOperatingSystemMXBean().getName());
			r.put("load average", ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
			r.put("#cores", Runtime.getRuntime().availableProcessors());
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
		public void createSwingComponents(Consumer<JComponent> c) throws IOException {
			c.accept(ByUtils.JsonToTreeConverter.buildTreeModel(toJSON(node)));
		}
	}

}
