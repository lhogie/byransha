package byransha.ui.swing;

import javax.swing.JTextArea;

import byransha.translate.Translator;

public class TranslatableTextArea extends JTextArea implements ComponentShowingTextAndToolTip {
	private Translator translator;

	public TranslatableTextArea(Translator v) {
		this.translator = v;
	}

	@Override
	public void setText(String s) {
		super.setText(translator.t(s));
	}

	@Override
	public void setToolTipText(String s) {
		super.setToolTipText(translator.t(s));
	}

}