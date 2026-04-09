package byransha.nodes.primitive;

import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import byransha.graph.BGraph;
import byransha.nodes.system.ChatNode;

public class BooleanNode extends PrimitiveValueNode<Boolean> {

	public BooleanNode(BGraph g, Boolean v) {
		super(g);
		set(v);
	}

	@Override
	public String whatIsThis() {
		return "a boolean value, (true of false)";
	}

	@Override
	public Boolean defaultValue() {
		return null;
	}

	@Override
	protected void writeValue(Boolean v, ObjectOutput out) throws IOException {
		out.write(v ? 1 : 0);
	}

	@Override
	protected Boolean readValue(ObjectInput in) throws IOException {
		return in.readInt() == 1;
	}

	@Override
	public JComponent getAsComponent(ChatNode pane) {
		return true ? checkbox(pane) : getComponent_radioButtons();
	}

	public JComponent checkbox(ChatNode pane) {
		var c = new JCheckBox();
		c.addActionListener(e -> set(c.isSelected()));
		valueChangeListeners.add((n, oldV, newV) -> c.setSelected(get()));
		return c;
	}

	public JComponent getComponent_radioButtons() {
		var p = new JPanel(new GridLayout(1, 2));
		var yes = new JRadioButton("yes");
		var no = new JRadioButton("no");
		var group = new ButtonGroup();
		group.add(yes);
		group.add(no);
		p.add(yes);
		p.add(no);
		yes.addActionListener(e -> set(true));
		no.addActionListener(e -> set(false));
		valueChangeListeners.add((node, oldV, newV) -> (newV ? yes : no).setSelected(true));
		return p;
	}

}
