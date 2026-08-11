package byransha.ui.swing;

import byransha.graph.Element;
import byransha.graph.Hub;

public class ChatPanelNode extends Element {
	public final ChatPanel panel;

	protected ChatPanelNode(Hub g, ChatPanel panel) {
		super(g, null);
		this.panel = panel;
	}

	@Override
	public String toString() {
		return "chat panel";
	}

	@Override
	public void createActions() {
		super.createActions();
		cachedActions.elements.add(new Explode(this));
	}
}