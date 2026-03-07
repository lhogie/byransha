package byransha.graph;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class ActionCategory<IN extends BNode> extends BNode {
	@Hide
	protected final IN inputNode;

	public ActionCategory(BGraph g, IN inputNode) {
		super(g);
		this.inputNode = inputNode;
	}

	@Override
	public void createActions() {
		inputNode.actions().forEach(a -> {
			if (a.getCategory() == this) {
				//by
			}
		});
		super.createActions();
	}

	@Override
	public ObjectNode toJSONNode() {
		var r = super.toJSONNode();
		return r;
	}

}
