package byransha.nodes.lab;

import byransha.graph.BNode;

public abstract class OnTheFlyNode<T extends BNode> extends BNode {

	private final BNode parent;

	protected OnTheFlyNode(BNode parent) {
		super(parent.g);
		this.parent = parent;
	}

	public abstract T compute();

	@Override
	public String whatIsThis() {
		return "on the fly";
	}

	@Override
	public String prettyName() {
		return "on the fly";
	}

}
