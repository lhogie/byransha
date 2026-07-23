package byransha;

import byransha.graph.BNode;
import byransha.nodes.system.Byransha;
import byransha.util.Version;

public class VersionNode extends BNode {
	public Version version = new Version();

	public VersionNode(Byransha b) {
		super(b);
		version.set("0.0.6");
	}

	@Override
	public String toString() {
		return version.toString();
	}
}
