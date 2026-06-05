package byransha;

import byransha.graph.BNode;
import byransha.nodes.system.Byransha;
import byransha.util.Version;

public class VersionNode extends BNode{
	Version version;
	
	protected VersionNode(Byransha b) {
		super(b);
	}

}
