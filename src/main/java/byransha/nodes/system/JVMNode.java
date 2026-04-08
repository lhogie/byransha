package byransha.nodes.system;

import java.lang.management.ManagementFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;

public class JVMNode extends SystemNode {

	public JVMNode(BGraph g) {
		super(g);
	}

	@Override
	public String toString() {
		return "JVM " + System.getProperty("java.version");
	}

	@Override
	public String whatIsThis() {
		return "the JVM running this application";
	}

	@Override
	public ObjectNode describeAsJSON() {
		var r = new ObjectNode(factory);
		r.put("heap size", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());

		var props = new ObjectNode(factory);
		r.set("properties", props);

		for (var e : System.getProperties().entrySet()) {
			props.put((String) e.getKey(), (String) e.getValue());
		}

		return r;
	}

}
