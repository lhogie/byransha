package byransha.graph.view;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.nodes.primitive.BooleanNode;
import byransha.ui.swing.ByranshaUserPane;

public class BooleanNodeView extends NodeView<BooleanNode> {

	public BooleanNodeView(BGraph g, BooleanNode node) {
		super(g, node);
	}

	@Override
	public JsonNode toJSON() {
		ObjectNode r = new ObjectNode(null);
		Boolean b = n.get();
		r.set("value", b == null ? new com.fasterxml.jackson.databind.node.TextNode("-")
				: com.fasterxml.jackson.databind.node.BooleanNode.valueOf(b));
		return r;
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		var yes = new JRadioButton("yes");
		var no = new JRadioButton("no");
		var group = new ButtonGroup();
		group.add(yes);
		group.add(no);

		yes.addActionListener(e -> n.set(true));
		no.addActionListener(e -> n.set(false));

		n.valueChangeListeners.add(newValue -> {
			if (newValue == null) {
				throw new RuntimeException("null value not allowed in boolean node");
			} else if (newValue == true) {
				yes.setSelected(true);
			} else {
				no.setSelected(true);
			}
		});

		pane.append(yes);
		pane.append(no);
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