package byransha;

public class ProblemInElement {
	final public Element node;
	final public String msg;

	public ProblemInElement(Element node, String msg) {
		this.node = node;
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "error in " + node + ": " + msg;
	}
}
