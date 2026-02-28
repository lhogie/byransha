package byransha.graph.view;

import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class JumpTo extends NodeView<BNode> {

	public JumpTo(BBGraph g, BNode node) {
		super(g, node);
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
	public void addTo(Consumer<JComponent> onComponentCreated) {
		var b = new JButton(node.prettyName());
		b.addActionListener(e -> currentUser().jumpTo(node));
		onComponentCreated.accept(b);
	}

	public boolean showInViewList() {
		return false;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}
}