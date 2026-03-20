package byransha.nodes.primitive;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.view.NodeView;
import byransha.ui.swing.ChatSheet;

public class BooleanRadioButtonEditor extends NodeView<BooleanNode> {

	public BooleanRadioButtonEditor(BGraph g, BooleanNode node) {
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
		var yes = new JRadioButton("yes");
		var no = new JRadioButton("no");
		var group = new ButtonGroup();
		group.add(yes);
		group.add(no);

		yes.addActionListener(e -> viewedNode.set(true));
		no.addActionListener(e -> viewedNode.set(false));
		viewedNode.changeListeners.add(n -> (viewedNode.get() ? yes : no).setSelected(true));

		pane.appendToCurrentFlow(yes);
		pane.appendToCurrentFlow(no);
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