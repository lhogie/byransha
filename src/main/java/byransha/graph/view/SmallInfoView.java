package byransha.graph.view;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class SmallInfoView extends NodeView<BNode> {

	public SmallInfoView(BBGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "basic info";
	}

	@Override
	public JsonNode toJSON(BNode n) {
		return new TextNode(n.prettyName());
	}

	@Override
	public JComponent createComponentImpl(BNode n) {
		return new JLabel(currentUser() + " - " + currentUser().currentNode().prettyName() + " ("
				+ currentUser().currentNode().whatIsThis() + ")");
	}


	@Override
	protected boolean allowsEditing() {
		return false;
	}

}