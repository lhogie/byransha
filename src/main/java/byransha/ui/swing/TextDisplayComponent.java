package byransha.ui.swing;

import byransha.translate.Translator;

public class TextDisplayComponent extends TranslatableTextArea {
	public TextDisplayComponent(Translator translator, String s) {
		super(translator);
		setText(s);
		setEditable(false);
		setBackground(null);
		setBorder(null);
		setOpaque(false);
	}

}
