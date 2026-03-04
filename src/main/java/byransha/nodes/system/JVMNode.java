package byransha.nodes.system;

import java.lang.management.ManagementFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.view.NodeView;

public class JVMNode extends SystemB {

	public JVMNode(BGraph g) {
		super(g);
	}

	@Override
	public String prettyName() {
		return "JVM " + System.getProperty("java.version");
	}

	@Override
	public String whatIsThis() {
		return "the JVM running this application";
	}

	@Override
	public void createViews() {
		cachedViews.add(new View(g, this));
		super.createViews();
	}

	public static class View extends NodeView<JVMNode> {

		public View(BGraph g, JVMNode jvm) {
			super(g, jvm);
		}

		@Override
		public JsonNode toJSON() {
			var r = new ObjectNode(factory);
			r.put("heap size", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());

			var props = new ObjectNode(factory);
			r.set("properties", props);

			for (var e : System.getProperties().entrySet()) {
				props.put((String) e.getKey(), (String) e.getValue());
			}

			return r;
		}

		@Override
		public String whatItShows() {
			return "describes the server's JVM";
		}

		@Override
		protected boolean allowsEditing() {
			return false;
		}
	}
}
