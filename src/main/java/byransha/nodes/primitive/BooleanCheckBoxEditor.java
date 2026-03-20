package byransha.nodes.primitive;

import javax.swing.JCheckBox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.view.NodeView;
import byransha.ui.swing.ChatSheet;

public class BooleanCheckBoxEditor extends NodeView<BooleanNode> {

	public BooleanCheckBoxEditor(BGraph g, BooleanNode node) {
		super(g, node);
	}

	@Override
	public JsonNode toJSON() {
		ObjectNode r = new ObjectNode(null);
		Boolean b = viewedNode.get();
		r.set("value", b == null ? new com.fasterxml.jackson.databind.node.TextNode("-")
				: com.fasterxml.jackson.databind.node.BooleanNode.valueOf(b));
		return r;
	}

	@Override
	public void writeTo(ChatSheet pane) {
		var c = new JCheckBox();
		c.addActionListener(e -> viewedNode.set(c.isSelected()));
		viewedNode.changeListeners.add(n -> c.setSelected(viewedNode.get()));
		pane.appendToCurrentFlow(c);
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