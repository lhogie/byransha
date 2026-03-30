package byransha.ui.swing;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;

import byransha.graph.BNode;

public class TranslatableButton extends JButton implements ComponentShowingTextAndToolTip {
	private BNode v;

	public TranslatableButton(BNode v) {
		this.v = v;
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	@Override
	public void setText(String s) {
		super.setText(v.t(s));
	}

	@Override
	public void setToolTipText(String s) {
		super.setToolTipText(v.t(s));
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