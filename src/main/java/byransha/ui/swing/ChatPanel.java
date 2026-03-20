package byransha.ui.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import byransha.graph.BNode;
import byransha.graph.action.Jump;
import byransha.nodes.system.ChatNode;
import byransha.util.Base62;

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

		topBar.add(addButton("clear chat", "ink_eraser_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24", e -> chat.elements.clear()));
		topBar.add(addButton("go the root node", "home_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24", e -> chat.append(chat.g.application)));
		topBar.add(addButton("settings", "settings_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24", e -> chat.append(chat.g)));
		topBar.add(addButton("jump to a specific node", "search_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24",
				e -> chat.append(new Jump(chat.g, chat))));
		topBar.add(addButton("see the current user", "user_attributes_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24",
				e -> chat.append(chat.g.currentUser())));
		topBar.add(addButton("authenticate a new user", "person_check_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24",
				e -> chat.append(chat.g.authenticator)));
		topBar.add(addButton("see the node corresponding to this chat", "chat_info_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24", e -> chat.append(chat)));
		topBar.add(addButton("starts a new chat", "add_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24",
				e -> new ChatNode(chat.currentUser())));
		topBar.add(addButton("closes this chat", "close_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24", e -> {
			chat.currentUser().chatList.elements.remove(chat);
			Container parent = getParent();
			parent.remove(this);
			parent.revalidate();
			parent.repaint();
			closeListener.onClose(this);
		}));

		var dropb = addButton("drop", null, e -> {});
		topBar.add(dropb);

		
		new DropTarget(dropb, new DropTargetAdapter() {
			@Override
			public void dragOver(DropTargetDragEvent dtde) {

			}

			@Override
			public void drop(DropTargetDropEvent e) {
				System.out.println("DROPPPED");
				try {
					var droppedNode = node(e);
					chat.elements.add(droppedNode);
					e.dropComplete(true);
				} catch (Exception ex) {
					e.dropComplete(false);
					chat.error(ex);
				}
			}

			private BNode node(DropTargetDropEvent e) throws UnsupportedFlavorException, IOException {
				String text = (String) e.getTransferable().getTransferData(DataFlavor.stringFlavor);
				long id = Base62.decode(text);
				return chat.g.indexes.byId.get(id);
			}
		});

		add(topBar, BorderLayout.NORTH);

	}

	JButton addButton(String text, ActionListener l) {
		return addButton(text, null, l);
	}

	JButton addButton(String text, String pathToIcon, ActionListener l) {
		JButton b = new JButton();
		b.setToolTipText(text);

		if (pathToIcon != null)
			b.setIcon(new ImageIcon(ChatPanel.class.getResource("icon/" + pathToIcon + ".png")));
		b.setPreferredSize(new Dimension(40, 40));
		b.setMargin(new Insets(0, 4, 0, 4));
		b.setFocusable(false);
		b.addActionListener(l);
		return b;
	}

	public void setCloseListener(CloseListener listener) {
		this.closeListener = listener;
	}
}