package byransha.ui.swing;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class ChatSheetNode extends BNode {

	protected ChatSheetNode(BGraph g) {
		super(g);
	}

	@Override
	public String toString() {
		return "chat sheet";
	}
}