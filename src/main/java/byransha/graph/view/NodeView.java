package byransha.graph.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import butils.ByUtils;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.javafx.Utils;
import byransha.ui.swing.ChatSheet;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

public abstract class NodeView<N extends BNode> extends BNode {
	public final N viewedNode;

	public NodeView(BGraph g, N node) {
		super(g);
		this.viewedNode = node;
	}

	public abstract JsonNode toJSON();

	@Override
	public ObjectNode toJSONNode() {
		var r = super.toJSONNode();
		r.put("showInMainWindow", showInViewList());
		r.put("whatItShows", whatItShows());
		return r;
	}

	public boolean showInViewList() {
		return true;
	}

	@Override
	public String whatIsThis() {
		return "a view showing " + whatItShows();
	}

	public abstract String whatItShows();

	@Override
	public String prettyName() {
		return ByUtils.camelToWords(getClass().getSimpleName()).replaceAll(" view", "");
	}

	public String technicalName() {
		return prettyName().replace(' ', '_').toLowerCase();
	}

	protected abstract boolean allowsEditing();

	protected boolean kishanable() {
		return false;
	}

	public void writeTo(ChatSheet pane) {
		var c = ByUtils.JsonToTreeConverter.buildTreeModel(toJSON());
		pane.appendToCurrentFlow(byransha.ui.swing.Utils.resizableScrollPane(c));
	}

	public void writeTo(Pane pane) {
		var rootItem = Utils.buildTree(toJSON());
		TreeView<String> treeView = new TreeView<>(rootItem);
		ScrollPane scrollPane = new ScrollPane(treeView);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setPrefWidth(500);
		scrollPane.setPrefHeight(300);
		treeView.setPrefWidth(500);
		treeView.setPrefHeight(300);
		scrollPane.setFitToWidth(true);
		pane.getChildren().add(scrollPane);
	}

}
