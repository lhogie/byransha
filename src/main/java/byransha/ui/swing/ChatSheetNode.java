package byransha.ui.swing;

import byransha.graph.Element;
import byransha.graph.Hub;

public class ChatSheetNode extends Element {

	protected ChatSheetNode(Hub g) {
		super(g, null);
	}

	@Override
	public String toString() {
		return "chat sheet";
	}
}