package byransha.graph.view;

import javax.swing.JLabel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ChatSheet;

public class SmallInfoView extends NodeView<BNode> {

	public SmallInfoView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "basic info";
	}

	@Override
	public JsonNode toJSON() {
		return new TextNode(viewedNode.prettyName());
	}

	@Override
	public void writeTo(ChatSheet pane) {
		pane.appendToCurrentFlow(new JLabel(viewedNode + " - " + viewedNode.prettyName() + " (" + viewedNode.whatIsThis() + ")"));
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

}