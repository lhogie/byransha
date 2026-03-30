package byransha.nodes.lab;

import byransha.graph.BNode;

public abstract class DynamicValuedNode<OUT extends BNode> extends BNode {
	public final BNode parent;

	protected DynamicValuedNode(BNode parent) {
		super(parent.g);
		this.parent = parent;
	}

	public abstract OUT exec();

	@Override
	public String whatIsThis() {
		return "a dynamic node";
	}

	@Override
	public String toString() {
		return "dynamic node";
	}
}
