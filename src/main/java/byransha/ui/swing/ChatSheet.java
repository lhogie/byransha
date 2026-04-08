package byransha.ui.swing;

import javax.swing.JSeparator;

import byransha.graph.BNode;
import byransha.nodes.system.ChatNode;

public class ChatSheet extends Sheet {
	public final ChatNode chat;

	public ChatSheet(ChatNode chat) {
		super();
		this.chat = chat;
//		Utils.idDropTarget(chat.g, this, n -> chat.nodes.elements.add(n));
	}

	void appendNode(BNode n) {
		if (!chat.nodes.elements.isEmpty()) {
			add(new JSeparator());
			newLine();
		}
		this.bgColor = n.getBackgroundColor();

		newLine();
		appendToCurrentLine(n.createBall(20, 0, chat));
		appendToCurrentLine(n + " (" + n.whatIsThis() + ")");
		newLine();
		newLine();
		n.writeTo(this);
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
