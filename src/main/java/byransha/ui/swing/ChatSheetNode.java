package byransha.ui.swing;

import byransha.Element;
import byransha.service.system.Hub;

public class ChatSheetNode extends Element {

	protected ChatSheetNode(Hub g) {
		super(g, null);
	}

	@Override
	public String toString() {
		return "chat sheet";
	}
}