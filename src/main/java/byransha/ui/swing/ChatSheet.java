package byransha.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import byransha.graph.BNode;
import byransha.graph.view.AvailableActionsView;
import byransha.graph.view.ErrorsView;
import byransha.nodes.system.ChatNode;
import byransha.util.Base62;
import byransha.util.ListChangeListener;

public class ChatSheet extends ScrollablePanel {

	JPanel currentFlow = createNewFlow();
	public final ChatNode chat;
//	int colorIndex;
//	Color[] backgroundColors = new Color[] { Color.white, new Color(0xEE, 0xEE, 0xEE, 10) };

	public ChatSheet(ChatNode chat) {
		super();
		this.chat = chat;
		setOpaque(false);
		var bl = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(bl);
		chat.get().forEach(node -> appendNode(node));

		chat.elements.listeners.add(new ListChangeListener<BNode>() {

			@Override
			public void onAdd(BNode n) {
				appendNode(n);
			}

			@Override
			public void onRemove(BNode n) {
				removeNode(n);
			}
		});

		new DropTarget(this, new DropTargetAdapter() {
			@Override
			public void dragOver(DropTargetDragEvent dtde) {

			}

			@Override
			public void drop(DropTargetDropEvent e) {
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

	}

	void appendNode(BNode n) {
		int nToRemove = 6;

		if (getComponentCount() > nToRemove) {
			for (int i = 0; i < nToRemove; ++i) {
				remove(getComponentCount() - 1);
			}
		}

		if (getComponentCount() > 0) {
			addSeparator();
		}

		this.bgColor = n.getBackgroundColor();

		newLine();
		appendToCurrentFlow(Utils.idShower(n, 20, 0));
		appendToCurrentFlow(n.prettyName() + " is " + n.whatIsThis());
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
		appendToCurrentFlow("What do you want to do?");
		newLine();

		n.findView(AvailableActionsView.class).writeTo(this);
		newLine();
		end();

		new Thread(() -> swipeDownInOneSecond()).start();
	}

	public void swipeDownInOneSecond() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		var scrollPane = ((JScrollPane) getParent().getParent());
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		int nbSteps = vertical.getMaximum() - vertical.getValue();

		if (nbSteps > 0) {
			int pauseDuration = 1000 / nbSteps;

			for (int i = vertical.getValue(); i < vertical.getMaximum(); ++i) {
//				System.out.println(vertical.getValue() + " -> " + vertical.getMaximum());
				vertical.setValue(i);
				revalidate();
				repaint();

				try {
					Thread.sleep(pauseDuration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			vertical.setValue(vertical.getMaximum());
			revalidate();
			repaint();

		}

	}

	public void appendToCurrentFlow(String s) {
		appendToCurrentFlow(new TextDisplayComponent(chat, s));
	}

	public void appendToCurrentFlow(JComponent c) {
//		c.setBackground(null);
//		c.setBorder(null);
//		c.setOpaque(true);
		currentFlow.add(c);
	}

	public void newLine() {
		add(currentFlow);
		currentFlow = createNewFlow();
	}

	public Color bgColor;

	private JPanel createNewFlow() {
		var wp = new WrapPanel();
		wp.setOpaque(true);
		wp.setBackground(bgColor);
//		wp.setBorder(null);
		return wp;
	}

	public void clear() {
		removeAll();
		revalidate();
		repaint();
	}

	public void end() {
		addSeparator();
		add(Box.createVerticalGlue());
	}

	public void addSeparator() {
		var separator = new JSeparator();
		separator.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
		newLine();
		newLine();
		add(separator);
	}

	public void removeNode(BNode n) {
		// TODO
	}

}
