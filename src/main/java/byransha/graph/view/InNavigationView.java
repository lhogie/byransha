package byransha.graph.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.swing.ByranshaUserPane;

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
	public void writeTo(ByranshaUserPane pane) {
		g.i.reverseNavigation.forEachInOf(n, in -> in.source().findView(JumpTo.class).writeTo(pane));
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