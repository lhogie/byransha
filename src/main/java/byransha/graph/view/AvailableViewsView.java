package byransha.graph.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ByranshaUserPane;

public class AvailableViewsView extends NodeView<BNode> {
	int edgeSize = 60;

	public AvailableViewsView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public JsonNode toJSON() {
		ArrayNode r = new ArrayNode(null);
		var views = n.views();
		views.forEach(v -> r.add(v.toJSONNode()));
		return r;
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		n.views().forEach(v -> {
			v.findView(JumpTo.class).writeTo(pane);
		});
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

	@Override
	public String whatItShows() {
		return "the views available on this node";
	}

}