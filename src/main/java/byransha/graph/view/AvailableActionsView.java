package byransha.graph.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ChatSheet;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;

public class AvailableActionsView extends NodeView<BNode> {
	int edgeSize = 60;

	public AvailableActionsView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public JsonNode toJSON() {
		ArrayNode r = new ArrayNode(null);
		var actions = viewedNode.actions();
		actions.forEach(a -> r.add(a.toJSONNode()));
		return r;
	}


	@Override
	public void writeTo(ChatSheet pane) {
		viewedNode.actions().forEach(a -> {
			a.findView(JumpToMe.class).writeTo(pane);
		});
	}

	@Override
	public void writeTo(Pane pane) {
		var flow = new TextFlow();

		class A {
			Class target;
			List<JumpToMe> actions = new ArrayList<>();
		}

		List<A> as = new ArrayList<>();

		viewedNode.actions().forEach(a -> {
//			a.getTarget();
			a.findView(JumpToMe.class).writeTo(flow);
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