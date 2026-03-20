package byransha.ui.swing;

import byransha.graph.BNode;

public class TextDisplayComponent extends TranslatableTextArea {
	public TextDisplayComponent(BNode n, String s) {
		super(n);
		setText(s);
		setEditable(false);
		setBackground(null);
		setBorder(null);
		setOpaque(false);
	}

}
