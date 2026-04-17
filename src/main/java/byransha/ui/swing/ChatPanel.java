package byransha.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import byransha.graph.BNode;
import byransha.nodes.system.ChatNode;
import byransha.util.ListenableList;

public class ChatPanel extends JPanel {

	public ChatPanelCloseListener closeListener;
	public final ChatSheet sheet;
	private ChatPanelNode node;

	public ChatPanel(ChatNode chat) {
		this.node = new ChatPanelNode(chat.g(), this);

		setLayout(new BorderLayout());
		setBackground(chat.g().swing.backgroundColor.get());
		setOpaque(false);

		{ // north
			JPanel topBar = new WrapPanel();
			topBar.setOpaque(false);
			topBar.add(node.createBall(20, 20, chat));
			add(topBar, BorderLayout.NORTH);
		}
		{ // center
			sheet = new ChatSheet(chat);
			chat.nodes.elements.forEach(node -> sheet.appendNode(node));
			chat.nodes.elements.addListener(new ListenableList.Listener<BNode>() {

				@Override
				public void onAdded(int index, BNode element) {
					if (index != chat.nodes.elements.size() - 1)
						throw new IllegalStateException("nodes should only be added at the end of the list");

					sheet.appendNode(element);
				}

				@Override
				public void onRemoved(int index, BNode oldElement) {
					if (index != chat.nodes.elements.size() - 1)
						throw new IllegalStateException("nodes should only be removed at the end of the list");

					sheet.removeNode(oldElement);
				}

				@Override
				public void onSet(int index, BNode oldElement, BNode newElement) {
					throw new UnsupportedOperationException("nodes should not be replaced");
				}
			});

			var scroll = new JScrollPane(sheet);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			add(scroll, BorderLayout.CENTER);
		}
		{ // south
			TranslatableButton dropb = new TranslatableButton(chat.g().translator);
			dropb.setText("Drop anything here");
			dropb.setToolTipText("anything you drop here will be appended to the sheet");
			dropb.setFocusable(false);
			dropb.setBorder(new EmptyBorder(new Insets(15, 0, 15, 0)));
			Utils.idDropTarget(chat.g(), dropb, droppedNode -> chat.nodes.elements.add(droppedNode));
			dropb.setOpaque(false);
			add(dropb, BorderLayout.SOUTH);
		}
	}

	JButton addButton(String text, ActionListener l) {
		return addButton(text, null, l);
	}

	JButton addButton(String text, String iconName, ActionListener l) {
		JButton b = new JButton();
		b.setToolTipText(text);

		if (iconName != null) {
			b.setIcon(Utils.icon(iconName, 1));
		}

		b.setPreferredSize(new Dimension(40, 40));
		b.setMargin(new Insets(0, 4, 0, 4));
		b.setFocusable(false);
		b.addActionListener(l);
		return b;
	}

	public void setCloseListener(ChatPanelCloseListener listener) {
		this.closeListener = listener;
	}
}