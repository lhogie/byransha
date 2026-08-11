package byransha.ui.swing;

import byransha.Chat;
import byransha.Element;

public class ChatSheet extends Sheet {
	public final Chat chat;

	public ChatSheet(Chat chat) {
		super();
		this.chat = chat;
		Utils.idDropTarget(chat.hub(), this, n -> chat.nodes.elements.add(n));
	}

	void appendNode(Element n) {
		this.bgColor = n.getBackgroundColor();

		newLine();
		newLine();

		var is = this;// new ChatSheet(chat);

		var path = n.path().elements;

		for (int i = 0; i < path.size(); ++i) {
			var e = path.get(i);
			is.appendToCurrentLine(e.createBall(18, 2, chat));
			is.appendToCurrentLine(e.toString());

			if (i < path.size() - 1) {
				is.appendToCurrentLine(">");
			}
		}

		is.currentLine.setBackground(chat.hub().swingInterface.getBackgroundColor());
		is.currentLine.setOpaque(true);

		// appendToCurrentLine(n + " (" + n.whatIsThis() + ")");
		is.newLine();
		is.newLine();
		n.writeKishanView(is);

		// appendToCurrentLine(is);
		newLine();

		revalidate();
		repaint();

		var scrollPane = Utils.getScrollPane(this);
		if (scrollPane != null) {
			ScrollUtils.scrollToBottomWhenReady(scrollPane, 2000, 100);
		}
	}

	public void appendToCurrentLine(String s) {
		super.appendToCurrentLine(s, chat.hub().translator);
	}

}
