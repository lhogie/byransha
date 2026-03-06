package byransha.graph.view;

import java.awt.Color;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ByranshaUserPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class AvailableActionsView extends NodeView<BNode> {
	int edgeSize = 60;

	public AvailableActionsView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public JsonNode toJSON() {
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
	public void writeTo(ByranshaUserPane pane) {
		n.actions().forEach(a -> {
			a.findView(JumpTo.class).writeTo(pane);
			pane.append( " ");
		});
	}
	
	@Override
	public void writeTo(Pane pane) {
		var flow = new TextFlow();
		n.actions().forEach(a -> {
			a.findView(JumpTo.class).writeTo(flow);
		});
		pane.getChildren().add(flow);
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