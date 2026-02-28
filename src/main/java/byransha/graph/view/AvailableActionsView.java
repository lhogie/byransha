package byransha.graph.view;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.swing.MyLayout;
import byransha.swing.MyLayout.Direction;

public class AvailableActionsView extends NodeView<BNode> {
	int edgeSize = 60;

	public AvailableActionsView(BBGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public JsonNode toJSON(BNode n) {
		ArrayNode r = new ArrayNode(null);
		var actions = n.actions();
		actions.forEach(a -> r.add(a.toJSONNode()));
		return r;
	}

	@Override
	public Color getColor() {
		return Color.pink;
	}

	@Override
	public JComponent createComponentImpl(BNode n) {
//		var p = new JPanel(new MyLayout(Direction.HORIZONTAL));
		var actions = n.actions();
		var p = new JPanel(new MyLayout(Direction.HORIZONTAL));
		actions.forEach(a -> p.add(a.findView(JumpTo.class).createComponent()));
		return p;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

	@Override
	public String whatItShows() {
		return "the actions available on this node";
	}

}