package byransha.ui.swing;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import byransha.graph.BNode;
import byransha.nodes.primitive.ValuedNode;
import byransha.nodes.primitive.ValuedNode.ValueChangeListener;

public class ErrorIndicator extends JLabel {
	final BNode n;

	public ErrorIndicator(BNode n) {
		this.n = n;
//		setBorder(new LineBorder(Color.black));
		setOpaque(false);
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
			setForeground(Color.green);
		} else {
			setText("!");
			setForeground(Color.red);
		}
	}
}
