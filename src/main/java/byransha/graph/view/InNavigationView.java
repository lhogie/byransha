package byransha.graph.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ChatSheet;

public class InNavigationView extends NodeView<BNode> {

	public InNavigationView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "INs";
	}

	@Override
	public JsonNode toJSON() {
		ObjectNode r = new ObjectNode(BNode.factory);

		return r;
	}

	@Override
	public void writeTo(ChatSheet pane) {
		g.indexes.reverseNavigation.forEachInOf(viewedNode, in -> in.source().findView(JumpTo.class).writeTo(pane));
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}
	

	@Override
	public boolean showInViewList() {
		return true;
	}

}