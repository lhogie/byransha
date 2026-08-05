package byransha.ui.swing;

import byransha.graph.Hub;
import byransha.graph.BNode;

public class ChatSheetNode extends BNode {

	protected ChatSheetNode(Hub g) {
		super(g);
	}

	@Override
	public String toString() {
		return "chat sheet";
	}
}