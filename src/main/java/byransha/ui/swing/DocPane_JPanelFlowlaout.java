package byransha.ui.swing;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DocPane_JPanelFlowlaout extends ScrollablePanel implements ByranshaUserPane {

	JPanel currentFlow = createNewFlow();

	public DocPane_JPanelFlowlaout() {
		super();
		var bl = new BoxLayout(this, BoxLayout.Y_AXIS);
//		var bl = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(bl);
	}

	@Override
	public void append(String s) {
		currentFlow.add(new JTextField(s));
	}

	@Override
	public void append(JComponent c) {
		currentFlow.add(c);
	}

	@Override
	public void newLine() {
		add(currentFlow);
		currentFlow = createNewFlow();
	}

	private static JPanel createNewFlow() {
		var p = new WrapPanel();
        p.setBorder(BorderFactory.createTitledBorder(""));
		return p;
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public void clear() {
		removeAll();
		revalidate();
		repaint();
	}

	@Override
	public void end() {
		append("this is the end");
		newLine();
		add(Box.createVerticalGlue());
	}

}
