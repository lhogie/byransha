package byransha.ui.swing;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.action.JumpToAnotherNode;
import byransha.ui.swing.action.Explode;
import byransha.ui.swing.action.ShowApplication;
import byransha.ui.swing.action.ShowSuperNode;

public class ChatPanelNode extends BNode {
	public final ChatPanel panel;

	protected ChatPanelNode(BGraph g, ChatPanel panel) {
		super(g);
		this.panel = panel;
	}

	@Override
	public String toString() {
		return "chat panel";
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new Explode(this));
		cachedActions.elements.add(new ShowSuperNode(this));
		cachedActions.elements.add(new ShowApplication(this));
		cachedActions.elements.add(new JumpToAnotherNode(this));
//		cachedActions.elements.add(chat);
		cachedActions.elements.add(new JumpToAnotherNode(this));
		super.createActions();
	}

}