package byransha.graph;

public abstract class ProcedureAction<IN extends BNode> extends Action {

	protected final IN inputNode;

	public ProcedureAction(IN inputNode, Class<? extends Category>... category) {
		super(inputNode.g, category);
		this.inputNode = inputNode;
	}
}
