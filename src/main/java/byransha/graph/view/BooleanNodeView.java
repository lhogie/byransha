package byransha.graph.view;

import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.BooleanNode;

public class BooleanNodeView extends NodeView<BooleanNode> {

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

	@Override
	public String whatItShows() {
		return "boolean editor";
	}

	
	@Override
	protected boolean allowsEditing() {
		return true;
	}

}