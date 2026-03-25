package byransha.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import byransha.graph.action.Jump;
import byransha.nodes.system.ChatNode;
import byransha.ui.ShellServer;

public class ChatPanel extends JPanel {

	public interface CloseListener {
		void onClose(ChatPanel panel);
	}

	public CloseListener closeListener;
	private final ChatSheet sheet;

	public ChatPanel(ChatNode chat) {
		setLayout(new BorderLayout());
		setBackground(chat.g.ui.backgroundColor.get());
		setOpaque(true);

		{
			var mousePanel = new JPanel(new BorderLayout());

			sheet = new ChatSheet(chat);
			var scroll = new JScrollPane(sheet);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			mousePanel.add(scroll, BorderLayout.CENTER);

			{
				JPanel topBar = new WrapPanel();
				topBar.setOpaque(false);

				topBar.add(addButton("clear chat", "ink_eraser_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24",
						e -> chat.nodes.elements.clear()));
				topBar.add(addButton("go the root node", "home_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24",
						e -> chat.append(chat.g.application)));
				topBar.add(addButton("settings", "settings_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24",
						e -> chat.append(chat.g)));
				topBar.add(addButton("jump to a specific node", "search_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24",
						e -> chat.append(new Jump(chat.g, chat))));
				topBar.add(addButton("see the current user", "user_attributes_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24",
						e -> chat.append(chat.g.currentUser())));
				topBar.add(addButton("authenticate a new user", "person_check_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24",
						e -> chat.append(chat.g.authenticator)));
				topBar.add(addButton("see the node corresponding to this chat",
						"chat_info_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24", e -> chat.append(chat)));
				topBar.add(addButton("starts a new chat", "add_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24",
						e -> new ChatNode(chat.currentUser())));
				mousePanel.add(topBar, BorderLayout.NORTH);
			}

			{
				TranslatableButton dropb = new TranslatableButton(chat.g.translator);
				dropb.setText("Drop anything here");
				dropb.setToolTipText("anything you drop here will be appended to the sheet");
				dropb.setFocusable(false);
				dropb.setBorder(new EmptyBorder(new Insets(15, 0, 15, 0)));
				Utils.IdDropTarget(chat.g, dropb, droppedNode -> chat.nodes.elements.add(droppedNode));
				dropb.setOpaque(false);
				mousePanel.add(dropb, BorderLayout.SOUTH);
			}

			add(mousePanel, BorderLayout.CENTER);
		}

		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				var s = new Socket("localhost", ShellServer.DEFAULT_PORT);
				var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));

				new Thread(() -> {
					while (true) {
						try {
							String l = in.readLine();

							if (l == null) {
								sheet.appendToCurrentLine("Server stopped");
								return;
							} else {
								sheet.appendToCurrentLine(l);
							}
						} catch (IOException err) {
							chat.error(err);
							sheet.appendToCurrentLine(err.getMessage());
						}
					}
				}).start();

				var chatInput = new JTextField();
				add(chatInput, BorderLayout.SOUTH);
				chatInput.addActionListener(i -> {
					out.println(chatInput.getText());
					out.flush();
					chatInput.setText("");
				});
			} catch (IOException err) {
				chat.error(err);
				sheet.appendToCurrentLine(err.getMessage());
			}
		}
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