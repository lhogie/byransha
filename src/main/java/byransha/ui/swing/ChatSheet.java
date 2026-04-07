package byransha.ui.swing;

import byransha.graph.BNode;
import byransha.graph.view.ErrorsView;
import byransha.nodes.system.ChatNode;

public class ChatSheet extends Sheet {
	public final ChatNode chat;

	public ChatSheet(ChatNode chat) {
		super();
		this.chat = chat;
		Utils.idDropTarget(chat.g, this, n -> chat.nodes.elements.add(n));
	}

	void appendNode(BNode n) {
		this.bgColor = n.getBackgroundColor();

		newLine();
		appendToCurrentLine(Utils.idShower(n, 20, 0, chat));
		appendToCurrentLine(n + " (" + n.whatIsThis() + ")");
		newLine();
		newLine();
		n.views().getFirst().writeTo(this);
		newLine();

		var err = n.findView(ErrorsView.class);

		if (err.showInViewList()) {
			err.writeTo(this);
			newLine();
		}

		newLine();
		newLine();
		end();

		revalidate();
		repaint();

		var scrollPane = Utils.getScrollPane(this);
		if (scrollPane != null) {
			ScrollUtils.scrollToBottomWhenReady(scrollPane, 2000, 100);
		}
	}

	public void appendToCurrentLine(String s) {
		super.appendToCurrentLine(s, chat.g.translator);
	}

}
