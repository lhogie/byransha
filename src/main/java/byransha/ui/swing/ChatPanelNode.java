package byransha.ui.swing;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class ChatPanelNode extends BNode {
	ChatSheetNode sheet;

	protected ChatPanelNode(BGraph g) {
		super(g);
	}

	@Override
	public String toString() {
		return "chat panel";
	}
}