package byransha.graph.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ChatSheet;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class ErrorsView extends NodeView<BNode> {

	public ErrorsView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "errors";
	}

	@Override
	public JsonNode toJSON() {
		var r = new ArrayNode(factory);

		for (var err : viewedNode.errors()) {
			r.add(err.msg);
		}

		return r;
	}

	@Override
	protected boolean kishanable() {
		return true;
	}

	@Override
	public void writeTo(ChatSheet pane) {
		for (var err : viewedNode.errors()) {
			pane.appendToCurrentFlow("Error: " + err.msg);
			pane.newLine();
		}
	}

	@Override
	public void writeTo(Pane pane) {
		viewedNode.errors().forEach(err -> pane.getChildren().add(new Text("Error: " + err.msg + "\n")));
	}

	public boolean showInViewList() {
		return !viewedNode.errors().isEmpty();
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}
}