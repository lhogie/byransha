package byransha.ui.swing;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import byransha.ai.QueryIA;
import byransha.graph.Action;
import byransha.graph.BNode;
import byransha.graph.list.action.FunctionAction;
import byransha.nodes.primitive.TextNode;
import byransha.nodes.system.ChatNode;

public class ChatSheet extends Sheet {
	public final ChatNode chat;

	public ChatSheet(ChatNode chat) {
		super();
		this.chat = chat;
		Utils.idDropTarget(chat.g(), this, n -> chat.nodes.elements.add(n));
	}

	void appendNode(BNode n) {
		if (getComponentCount() > 1) {
			add(new JSeparator());
			newLine();
		}
		
		this.bgColor = n.getBackgroundColor();

		newLine();
		appendToCurrentLine("path:");
		n.path().elements.forEach(e -> {
			appendToCurrentLine(e.createBall(20, 20, chat));
			appendToCurrentLine(e.toString());
		});
		newLine();
		appendToCurrentLine(n + " (" + n.whatIsThis() + ")");
		newLine();
		newLine();
		n.writeKishanView(this);

		if (n instanceof Action action) {
			newLine();
			JTextField queryPromptField = null;

			if (action instanceof QueryIA queryIA) {
				queryPromptField = new JTextField(queryIA.prompt.get() == null ? "" : queryIA.prompt.get(), 28);
				appendToCurrentLine(queryPromptField);

				var jsonOnly = createResponseModeBubble("JSON only");
				var Conversation = createResponseModeBubble("Conversation");

				var group = new ButtonGroup();
				group.add(jsonOnly);
				group.add(Conversation);

				if (queryIA.getResponseMode() == QueryIA.ResponseMode.CONVERSATION) {
					Conversation.setSelected(true);
					
				} else {
					jsonOnly.setSelected(true);
				}

				jsonOnly.addActionListener(e -> queryIA.setResponseMode(QueryIA.ResponseMode.JSON_ONLY));
				Conversation.addActionListener(e -> queryIA.setResponseMode(QueryIA.ResponseMode.CONVERSATION));

				appendToCurrentLine(jsonOnly);
				appendToCurrentLine(Conversation);
			}

			var b = new JButton("Ok");
			final JTextField finalQueryPromptField = queryPromptField;
			b.addActionListener(e -> {
				if (action.isRunning()) {
					return;
				}

				if (action instanceof QueryIA queryIAAction && finalQueryPromptField != null) {
					queryIAAction.prompt.set(finalQueryPromptField.getText());
				}

				b.setEnabled(false);
				b.setText("Running...");

				TextNode liveResponseNode = null;
				final StringBuilder liveResponseText = new StringBuilder();

				if (action instanceof FunctionAction<?, ?>) {
					liveResponseNode = new TextNode(chat.g(), "IA response", "");
					chat.append(liveResponseNode);

					final var targetNode = liveResponseNode;
					action.outputConsumer = chunk -> {
						if (chunk == null) {
							return;
						}

						final String snapshot;
						synchronized (liveResponseText) {
							liveResponseText.append(String.valueOf(chunk));
							snapshot = liveResponseText.toString();
						}

						SwingUtilities.invokeLater(() -> targetNode.set(snapshot));
					};
				}

				action.chat = chat;
				action.execAsync();

				final TextNode finalLiveResponseNode = liveResponseNode;
				new Thread(() -> {
					action.waitForCompletion();

					SwingUtilities.invokeLater(() -> {
						try {
							if (action instanceof FunctionAction<?, ?> fa && fa.result != null) {
								if (finalLiveResponseNode != null && fa.result instanceof TextNode tn) {
									finalLiveResponseNode.set(tn.get());
								} else {
									chat.append(fa.result);
								}
							}
						} finally {
							b.setText("Ok");
							b.setEnabled(true);
						}
					});
				}, action.technicalName() + "-ui-waiter").start();
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
		super.appendToCurrentLine(s, chat.g().translator);
	}

	private JToggleButton createResponseModeBubble(String text) {
		var bubble = new JToggleButton(text);
		bubble.setFocusPainted(false);
		bubble.setMargin(new Insets(4, 10, 4, 10));
		bubble.setBackground(new Color(0xF2, 0xF2, 0xF2));
		bubble.setOpaque(true);
		return bubble;
	}

}
