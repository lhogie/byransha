package byransha.ui.swing;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import byransha.graph.BNode;
import byransha.graph.view.AvailableActionsView;
import byransha.graph.view.ErrorsView;

public class ByranshaUserPane extends ScrollablePanel {

	JPanel currentFlow = createNewFlow();

	public ByranshaUserPane() {
		super();
		var bl = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(bl);
	}

	public void addNode(BNode n) {
//		clear();
		appendToCurrentFlow(
				"<html><b><h3>" + n.prettyName() + "\" is " + n.whatIsThis() + ". Its ID is " + n.id() + ".");
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
		var scrollPane = ((JScrollPane) getParent().getParent());
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
		revalidate();
		repaint();
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
		wp.setBorder(BorderFactory.createTitledBorder(""));
		return wp;
	}
	private static JPanel createNewFlow2() {
		var wp = new WrapPanel();
		var cp = new ClosablePanel(wp);
		cp.setBorder(BorderFactory.createTitledBorder(""));
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
