package byransha.ui.swing;

import javax.swing.JTextArea;

import byransha.graph.BNode;

public class TranslatableTextArea extends JTextArea implements Translatable {
	private BNode v;

	public TranslatableTextArea(BNode v) {
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