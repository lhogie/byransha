package byransha.graph.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ByranshaUserPane;

public class OutNavigationView extends NodeView<BNode> {

	public OutNavigationView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "OUTs";
	}

	@Override
	public JsonNode toJSON() {
		ObjectNode r = new ObjectNode(BNode.factory);

		return r;
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		viewedNode.forEachOut((out, role) -> {
			for (var v : out.views()) {
				if (v instanceof JumpTo j) {
					j.writeTo(pane);
					return;
				}
			}
			throw new IllegalStateException(out.getClass() + " has no jump view");
		});

	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

}