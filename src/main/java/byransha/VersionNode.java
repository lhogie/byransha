package byransha;

import byransha.graph.Element;
import byransha.system.Byransha;
import byransha.util.Version;

public class VersionNode extends Element {
	public Version version = new Version();

	public VersionNode(Byransha b) {
		super(b, null);
		version.set(Byransha.VERSION);
	}

	@Override
	public String toString() {
		return version.toString();
	}
}
