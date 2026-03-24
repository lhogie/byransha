package byransha.ui.swing;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;

import byransha.graph.BNode;
import byransha.nodes.primitive.ValuedNode;
import byransha.nodes.primitive.ValuedNode.ValueChangeListener;

public class ErrorIndicator extends JLabel {
	final BNode n;

	public ErrorIndicator(BNode n) {
		this.n = n;
		setPreferredSize(new Dimension(20, 20));
		setBorder(null);
		setOpaque(false);
		setFocusable(false);
		setForeground(Color.red);
		update();
		n.changeListeners.add(c -> update());

		if (n instanceof ValuedNode vn) {
			vn.valueChangeListeners.add(new ValueChangeListener() {

				@Override
				public void changed(ValuedNode n, Object formerValue, Object newValue) {
					update();
				}
			});
		}
	}

	private void update() {
		if (n.errors().isEmpty()) {
			setText("");
		} else {
			setText("!");
		}
	}
}
