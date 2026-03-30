package byransha.graph.view;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.Sheet;

public class AvailableViewsView extends NodeView<BNode> {
	int edgeSize = 60;

	public AvailableViewsView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public void writeTo(Sheet pane) {
		viewedNode.views().forEach(v -> v.findView(JumpToMe.class).writeTo(pane));
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