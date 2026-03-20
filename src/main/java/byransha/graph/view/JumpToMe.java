package byransha.graph.view;

import javax.swing.JButton;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ChatSheet;
import javafx.scene.layout.Pane;

public class JumpToMe extends NodeView<BNode> {
	private String label;
	private JButton b;

	public JumpToMe(BGraph g, BNode node) {
		super(g, node);
	}

	protected boolean kishanable() {
		return true;
	}

	@Override
	public String whatItShows() {
		return "a way to jump to the node";
	}

	public void setLabel(String l) {
		this.label = l;

		if (b != null) {
			b.setText(label);
		}
	}

	@Override
	public JsonNode toJSON() {
		return new com.fasterxml.jackson.databind.node.IntNode(viewedNode.id());
	}

	@Override
	public void writeTo(ChatSheet pane) {
		pane.appendToCurrentFlow(viewedNode.createJumpButton(pane.chat));
	}

	@Override
	public void writeTo(Pane pane) {

	}

	public boolean showInViewList() {
		return false;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}
}