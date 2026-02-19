package byransha.nodes.primitive;

import java.awt.FlowLayout;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.graph.NodeView;
import byransha.nodes.system.User;

public class BooleanNode extends PrimitiveValueNode<Boolean> {

	public BooleanNode(BBGraph g, Boolean v) {
		super(g);
		set(v);
	}

	@Override
	public String prettyName() {
		return get().toString();
	}

	@Override
	protected Boolean bytesToValue(byte[] bytes) throws IOException {
		if (bytes.length != 1)
			throw new IOException("Invalid byte array length for BooleanNode: " + bytes.length);

		if (bytes[0] == 0) {
			return false;
		} else if (bytes[0] == 1) {
			return true;
		} else {
			return null;
		}
	}

	@Override
	protected byte[] valueToBytes(Boolean b) throws IOException {
		var r = new byte[1];

		if (b == null) {
			r[0] = 2;
		} else {
			if (b) {
				r[0] = 1;
			} else {
				r[0] = 0;
			}
		}

		return r;
	}

	@Override
	public void fromString(String s) {
		set(Boolean.valueOf(s));
	}

	@Override
	public String whatIsThis() {
		return "a boolean value, (true of false)";
	}

	@Override
	public Boolean defaultValue() {
		return null;
	}

	public static class BooleanNodeView extends NodeView<BooleanNode> {

		protected BooleanNodeView(BBGraph g) {
			super(g);
		}

		@Override
		public JsonNode toJSON(BooleanNode n) {
			ObjectNode r = new ObjectNode(null);
			Boolean b = n.get();
			r.set("value", b == null ? new com.fasterxml.jackson.databind.node.TextNode("-")
					: com.fasterxml.jackson.databind.node.BooleanNode.valueOf(b));
			return r;
		}

		@Override
		public JComponent createComponentImpl(BooleanNode n) {
			boolean b = n.get();

			var yes = new JRadioButton("yes");
			var no = new JRadioButton("no");
			var dunno = new JRadioButton("don't know");
			var group = new ButtonGroup();
			group.add(yes);
			group.add(no);
			group.add(dunno);

			yes.addActionListener(e -> n.set(true));
			no.addActionListener(e -> n.set(false));
			dunno.addActionListener(e -> n.set(null));

			n.listeners.add(newValue -> {
				if (newValue == null) {
					dunno.setSelected(true);
				} else if (newValue == true) {
					yes.setSelected(true);
				} else {
					no.setSelected(true);
				}
			});

			var p = new JPanel(new FlowLayout());
			p.add(yes);
			p.add(no);
			p.add(dunno);

			return p;
		}
	}

}
