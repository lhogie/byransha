package byransha.graph.view;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class JumpToView extends NodeView<BNode> {

	public JumpToView(BBGraph g) {
		super(g);
	}

	protected boolean kishanable() {
		return true;
	}

	@Override
	public String whatItShows() {
		return "a way to jump to the node";
	}

	@Override
	public JsonNode toJSON(BNode n) {
		return new com.fasterxml.jackson.databind.node.IntNode(n.id());
	}

	@Override
	public JComponent createComponentImpl(BNode n) {
		var b = new JButton(n.prettyName());
		b.addActionListener(e -> currentUser().jumpTo(n));
		return b;
	}

	public boolean showInViewList() {
		return false;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}
}