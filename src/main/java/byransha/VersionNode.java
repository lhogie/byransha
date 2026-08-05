package byransha;

import byransha.graph.BNode;
import byransha.system.Byransha;
import byransha.util.Version;

public class VersionNode extends BNode {
	public Version version = new Version();

	public VersionNode(Byransha b) {
		super(b);
		version.set(Byransha.VERSION);
	}

	@Override
	public String toString() {
		return version.toString();
	}
}
