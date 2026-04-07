package byransha.graph.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ChatSheet;

public class OutNavigationView extends NodeView<BNode> {

	public OutNavigationView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "OUTs";
	}

	@Override
	public JsonNode jsonView() {
		ObjectNode r = new ObjectNode(BNode.factory);
		viewedNode.forEachOut(o -> r.set("actions", o.describeAsJSON().get("actions")));
		viewedNode.forEachOut(o -> r.set("outs", o.describeAsJSON().get("outs")));
		return r;
	}

	@Override
	public void writeTo(ChatSheet pane) {
		viewedNode.forEachOut((out, role) -> {
			for (var v : out.views()) {
				if (v instanceof JumpToMe j) {
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