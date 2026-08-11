package byransha.ui.swing;

import java.awt.GridLayout;

import javax.swing.JPanel;

import byransha.Chat;
import byransha.action.Category;
import byransha.action.ProcedureAction;

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
		var newChat = new Chat(hub().currentUser());
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
