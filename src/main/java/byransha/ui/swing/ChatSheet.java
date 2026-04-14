package byransha.ui.swing;

import javax.swing.JButton;
import javax.swing.JSeparator;

import byransha.graph.Action;
import byransha.graph.BNode;
import byransha.graph.list.action.FunctionAction;
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
		n.writeKishanView(this);

		if (n instanceof Action action) {
			newLine();
			var b = new JButton("Ok");
			b.addActionListener(e -> {
				try {
					action.execSync();

					if (n instanceof FunctionAction fa) {
						chat.append(fa.result);
					}
				} catch (Throwable err) {
					chat.append(chat.error(err, false));
				}
			});
			appendToCurrentLine(b);
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
