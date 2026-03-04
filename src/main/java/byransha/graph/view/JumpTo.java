package byransha.graph.view;

import java.awt.Dimension;

import javax.swing.JButton;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.swing.ByranshaUserPane;

public class JumpTo extends NodeView<BNode> {

	private String label;
	private JButton b;

	public JumpTo(BGraph g, BNode node) {
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
		return new com.fasterxml.jackson.databind.node.IntNode(n.id());
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		b = new JButton(label != null ? label : n.prettyName());
		b.setPreferredSize(new Dimension(100, 30));
		b.addActionListener(e -> currentUser().jumpTo(n));
		pane.append(b);
	}

	public boolean showInViewList() {
		return false;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}
}