package byransha.ui.swing.action;

import java.awt.GridLayout;

import javax.swing.JPanel;

import byransha.graph.Category;
import byransha.graph.ProcedureAction;
import byransha.nodes.system.ChatNode;
import byransha.ui.swing.ChatPanel;
import byransha.ui.swing.ChatPanelNode;

public class Explode extends ProcedureAction<ChatPanelNode> {

	public Explode(ChatPanelNode p) {
		super(p, Category.chatpanel.class);
	}

	@Override
	public String whatItDoes() {
		return "explode";
	}

	@Override
	protected void impl() throws Throwable {
		var container = inputNode.panel.getParent();
		int i = container.getComponentZOrder(inputNode.panel);
		var newPanel = new JPanel(new GridLayout(1, 2));
		newPanel.add(inputNode.panel);
		var newChat = new ChatNode(parent.currentUser());
		newPanel.add(new ChatPanel(newChat));
		container.add(newPanel, i);
		container.revalidate();
		container.repaint();
	}

	@Override
	public boolean applies() {
		return true;
	}

}
