package byransha.ui.swing;

import javax.swing.JButton;

import byransha.graph.BNode;

public class TranslatableButton extends JButton implements ComponentShowingTextAndToolTip {
	private BNode v;

	public TranslatableButton(BNode v) {
		this.v = v;
	}

	@Override
	public void setText(String s) {
		super.setText(v.t(s));
	}

	@Override
	public void setToolTipText(String s) {
		super.setToolTipText(v.t(s));
	}
}