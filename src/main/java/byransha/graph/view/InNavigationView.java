package byransha.graph.view;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.Sheet;

public class InNavigationView extends NodeView<BNode> {

	public InNavigationView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "INs";
	}

	@Override
	public ObjectNode describeAsJSON() {
		ObjectNode r = new ObjectNode(BNode.factory);

		return r;
	}

	@Override
	public void writeTo(Sheet pane) {
		g.indexes.reverseNavigation.forEachInOf(viewedNode, in -> in.source().findView(JumpToMe.class).writeTo(pane));
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