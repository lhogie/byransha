package byransha.ui.swing;

import java.awt.Dimension;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import byransha.graph.BNode;
import byransha.graph.view.AvailableActionsView;
import byransha.graph.view.ErrorsView;
import byransha.nodes.system.ChatNode;

public class ChatSheet extends ScrollablePanel {

	JPanel currentFlow = createNewFlow();
	public final ChatNode chat;
	public final JScrollPane scroll;

	public ChatSheet(ChatNode chat) {
		super();
		this.chat = chat;
		var bl = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(bl);
		this.scroll = new JScrollPane(this);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(new TitledBorder(chat.prettyName()));

		chat.get().forEach(c -> addNode(c));
	}

	public void addNode(BNode n) {
//		clear();
		var idLabel = new JTextField();
		idLabel.setEditable(false);
		DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(idLabel, DnDConstants.ACTION_COPY, e -> {
			String dragText = ((JTextField) e.getComponent()).getText();
			e.startDrag(DragSource.DefaultCopyDrop, new StringSelection(dragText));
		});

		appendToCurrentFlow(
				"<html><b><h3>" + n.prettyName() + "\" is " + n.whatIsThis() + ". Its ID is " + n.idAsText());
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
		appendToCurrentFlow("<html><i>What do <b>you</b> want to do?");
		newLine();

		n.findView(AvailableActionsView.class).writeTo(this);
		newLine();
		end();

		swipeDownInOneSecond();
	}

	public void swipeDownInOneSecond() {
		var scrollPane = ((JScrollPane) getParent().getParent());
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		int nbSteps = vertical.getMaximum() - vertical.getValue();
		System.out.println(nbSteps);
		int pauseDuration = 1 / nbSteps;

		for (int i = vertical.getValue(); i < vertical.getMaximum(); ++i) {
			System.out.println(vertical.getValue() + " -> " + vertical.getMaximum());
			vertical.setValue(i);
			revalidate();
			repaint();

			try {
				Thread.sleep(pauseDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void appendToCurrentFlow(String s) {
		var tf = new JEditorPane("text/html", s);
		tf.setEditable(false);
		tf.setBackground(null);
		tf.setBorder(null);
		tf.setOpaque(false);
		appendToCurrentFlow(tf);
	}

	public void appendToCurrentFlow(JComponent c) {
		currentFlow.add(c);
	}

	public void newLine() {
		add(currentFlow);
		currentFlow = createNewFlow();
	}

	private static JPanel createNewFlow() {
		var wp = new WrapPanel();
//		wp.setBorder(BorderFactory.createTitledBorder(""));
		return wp;
	}

	private static JPanel createNewFlow2() {
		var wp = new WrapPanel();
		var cp = new ClosablePanel(wp);
//		cp.setBorder(BorderFactory.createTitledBorder(""));
		return cp;
	}

	public void clear() {
		removeAll();
		revalidate();
		repaint();
	}

	public void end() {
		var separator = new JSeparator();
		separator.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
		add(separator);
		add(Box.createVerticalGlue());
	}

}
