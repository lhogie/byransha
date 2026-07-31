package byransha.ui.swing;

import byransha.graph.Root;
import byransha.graph.BNode;
import byransha.ui.swing.action.Explode;

public class ChatPanelNode extends BNode {
	public final ChatPanel panel;

	protected ChatPanelNode(Root g, ChatPanel panel) {
		super(g);
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