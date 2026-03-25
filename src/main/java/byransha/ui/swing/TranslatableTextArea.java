package byransha.ui.swing;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.ToolTipManager;

import byransha.translate.Translator;

public class TranslatableTextArea extends JTextArea implements ComponentShowingTextAndToolTip {
	private Translator translator;

	public TranslatableTextArea(Translator v) {
		this.translator = v;
		ToolTipManager.sharedInstance().registerComponent(this);

	}

	@Override
	public void setText(String s) {
		super.setText(translator.t(s));
	}

	@Override
	public void setToolTipText(String s) {
		super.setToolTipText(translator.t(s));
	}

	@Override
	public String getToolTipText() {
		String tip = super.getToolTipText();
		if (tip == null) {
			for (Component c = getParent(); c instanceof JComponent jc; c = c.getParent()) {
				tip = jc.getToolTipText();
				if (tip != null)
					return tip;
			}
		}
		return tip;
	}
}