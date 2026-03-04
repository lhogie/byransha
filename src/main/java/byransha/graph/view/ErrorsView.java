package byransha.graph.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.swing.ByranshaUserPane;

public class ErrorsView extends NodeView<BNode> {

	public ErrorsView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "errors";
	}

	@Override
	public JsonNode toJSON() {
		var r = new ArrayNode(factory);

		for (var err : n.errors()) {
			r.add(err.msg);
		}

		return r;
	}

	@Override
	protected boolean kishanable() {
		return true;
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		for (var err : n.errors()) {
			pane.append("Error: " + err.msg);
			pane.newLine();
		}
	}

	public boolean showInViewList() {
		return !n.errors().isEmpty();
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}
}