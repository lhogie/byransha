package byransha.graph.view;

import java.awt.Color;
import java.io.IOException;
import java.util.function.Consumer;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

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
	public void createSwingComponents(Consumer<JComponent> onComponentCreated) {
		node.actions().forEach(a -> {
			a.views().forEach(v -> {
				if (v instanceof JumpTo jt) {
					jt.createSwingComponents(onComponentCreated);
				}
			});
		});
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