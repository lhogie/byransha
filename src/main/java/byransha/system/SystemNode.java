package byransha.system;

import java.util.UUID;

import byransha.graph.BNode;

public abstract class SystemNode extends BNode {

	public SystemNode(BNode parent) {
		super(parent);
		super.setID(new UUID(0, getClass().hashCode()));
	}

	@Override
	public void setID(UUID newID) {
		throw new UnsupportedOperationException();
	};
}
