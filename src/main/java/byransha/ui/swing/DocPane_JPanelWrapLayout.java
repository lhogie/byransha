package byransha.ui.swing;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DocPane_JPanelWrapLayout extends JPanel implements ByranshaUserPane {

	public DocPane_JPanelWrapLayout() {
		super(new WrapLayout(5, 5));
	}

	@Override
	public void append(String s) {
		add(new JTextField(s));
	}

	@Override
	public void append(JComponent c) {
		add(c);
	}

	@Override
	public void newLine() {
		add(WrapLayout.createLineBreak());
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
		 add(Box.createVerticalGlue());
	}

}
