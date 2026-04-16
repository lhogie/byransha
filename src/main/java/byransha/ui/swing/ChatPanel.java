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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import byransha.graph.BNode;
import byransha.graph.action.JumpToAnotherNode;
import byransha.nodes.system.ChatNode;
import byransha.ui.shell.ShellServer;
import byransha.util.ListenableList;

public class ChatPanel extends JPanel {

	public ChatPanelCloseListener closeListener;
	public final ChatSheet sheet;
	private ChatPanelNode node;

	public ChatPanel(ChatNode chat) {
		this.node = new ChatPanelNode(chat.g, this);

		setLayout(new BorderLayout());
		setBackground(chat.g.swing.backgroundColor.get());
		setOpaque(true);

		{
			var mousePanel = new JPanel(new BorderLayout());
			mousePanel.setOpaque(false);

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
			mousePanel.add(scroll, BorderLayout.CENTER);

			{
				JPanel topBar = new WrapPanel();
				topBar.setOpaque(false);
				topBar.add(node.createBall(20, 20, chat));
				mousePanel.add(topBar, BorderLayout.NORTH);
			}

			{
				TranslatableButton dropb = new TranslatableButton(chat.g.translator);
				dropb.setText("Drop anything here");
				dropb.setToolTipText("anything you drop here will be appended to the sheet");
				dropb.setFocusable(false);
				dropb.setBorder(new EmptyBorder(new Insets(15, 0, 15, 0)));
				Utils.idDropTarget(chat.g, dropb, droppedNode -> chat.nodes.elements.add(droppedNode));
				dropb.setOpaque(false);
				mousePanel.add(dropb, BorderLayout.SOUTH);
			}

			add(mousePanel, BorderLayout.CENTER);
		}

		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				var s = new Socket("localhost", ShellServer.DEFAULT_PORT);
				var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));

				new Thread(() -> {
					while (true) {
						try {
							String serverInput = in.readLine();

							if (serverInput == null) {
								sheet.appendToCurrentLine("Server stopped");
								return;
							} else {
								sheet.appendToCurrentLine(serverInput);
							}
						} catch (IOException err) {
							chat.error(err);
							sheet.appendToCurrentLine(err.getMessage());
						}
					}
				}).start();

				out.println(chat.id());
				out.flush();

				var chatInput = new JTextField();
				chatInput.setOpaque(false);
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