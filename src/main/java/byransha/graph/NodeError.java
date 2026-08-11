package byransha.graph;

public class NodeError {
	final public Element node;
	final public String msg;

	public NodeError(Element node, String msg) {
		this.node = node;
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "error in " + node + ": " + msg;
	}
}
