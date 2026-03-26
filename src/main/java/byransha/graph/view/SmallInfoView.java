package byransha.graph.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.Sheet;

public class SmallInfoView extends NodeView<BNode> {

	public SmallInfoView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "basic info";
	}

	@Override
	public JsonNode jsonView() {
		return new TextNode(viewedNode.toString());
	}

	@Override
	public void writeTo(Sheet pane) {
		pane.appendToCurrentLine(viewedNode + " - " + viewedNode + " (" + viewedNode.whatIsThis() + ")",
				g.translator);
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

}