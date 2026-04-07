package byransha.nodes.primitive;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;

public class BooleanCheckBoxEditor extends TradUINodeView<BooleanNode> {

	public BooleanCheckBoxEditor(BGraph g, BooleanNode node) {
		super(g, node);
	}

	@Override
	public JsonNode jsonView() {
		ObjectNode r = new ObjectNode(null);
		Boolean b = viewedNode.get();
		r.set("value", b == null ? new com.fasterxml.jackson.databind.node.TextNode("-")
				: com.fasterxml.jackson.databind.node.BooleanNode.valueOf(b));
		return r;
	}

	@Override
	public String whatItShows() {
		return "boolean editor";
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

	@Override
	public JComponent getComponent() {
		var c = new JCheckBox();
		c.addActionListener(e -> viewedNode.set(c.isSelected()));
		viewedNode.valueChangeListeners.add((n, oldV, newV) -> c.setSelected(viewedNode.get()));
		return c;
	}

}