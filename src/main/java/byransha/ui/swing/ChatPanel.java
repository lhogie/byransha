package byransha.ui.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import byransha.graph.action.Jump;
import byransha.nodes.system.ChatNode;

public class ChatPanel extends JPanel {

	public interface CloseListener {
		void onClose(ChatPanel panel);
	}

	public CloseListener closeListener;

	public ChatPanel(ChatNode chat) {
		setLayout(new BorderLayout());
		setOpaque(false);

		var sheet = new ChatSheet(chat);
		var scroll = new JScrollPane(sheet);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, BorderLayout.CENTER);

		
		JPanel topBar = new WrapPanel();
		topBar.setOpaque(false);

		topBar.add(addButton("clear", e -> chat.elements.clear()));
		topBar.add(addButton("H", e -> chat.append(chat.g.application)));
		topBar.add(addButton("G", e -> chat.append(chat.g)));
		topBar.add(addButton("J", e -> chat.append(new Jump(chat.g, chat))));
		topBar.add(addButton("U", e -> chat.append(chat.g.currentUser())));
		topBar.add(addButton("Auth", e -> chat.append(chat.g.authenticator)));
		topBar.add(addButton("this", e -> chat.append(chat)));
		topBar.add(addButton("+", e -> new ChatNode(chat.currentUser(), chat.currentUser())));
		topBar.add(addButton("-", e -> {
			chat.currentUser().chatList.elements.remove(chat);
			Container parent = getParent();
			parent.remove(this);
			parent.revalidate();
			parent.repaint();
			closeListener.onClose(this);
		}));

		add(topBar, BorderLayout.NORTH);

	}

	JButton addButton(String text, ActionListener l) {
		JButton closeBtn = new JButton(text);
		closeBtn.setPreferredSize(new Dimension(40, 40));
		closeBtn.setMargin(new Insets(0, 4, 0, 4));
		closeBtn.setFocusable(false);
		closeBtn.addActionListener(l);
		return closeBtn;
	}

	public void setCloseListener(CloseListener listener) {
		this.closeListener = listener;
	}
}