package byransha.graph.view;

import java.awt.Color;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.javafx.Utils;
import byransha.ui.swing.ChatSheet;
import byransha.ui.swing.Sheet;
import byransha.util.ByUtils;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

public abstract class NodeView<N extends BNode> extends BNode {
	public final N viewedNode;

	public NodeView(BGraph g, N node) {
		super(g);
		this.viewedNode = node;
	}

	public boolean showInViewList() {
		return true;
	}

	public JsonNode jsonView() {
		return viewedNode.describeAsJSON();
	}

	@Override
	public String whatIsThis() {
		return "a view showing " + whatItShows();
	}

	public abstract String whatItShows();

	@Override
	public String toString() {
		return ByUtils.camelToWords(getClass().getSimpleName()).replaceAll(" view", "");
	}

	public String technicalName() {
		return toString().replace(' ', '_').toLowerCase();
	}

	protected abstract boolean allowsEditing();

	protected boolean kishanable() {
		return false;
	}

	public JComponent getJSONDisplayComponent() {
		return ByUtils.JsonToTreeConverter.buildTreeModel(describeAsJSON());
	}

	public void writeTo(Sheet pane) {
		byransha.ui.swing.Utils.resizableScrollPane(getJSONDisplayComponent());
	}

	public void writeTo(Pane pane) {
		var rootItem = Utils.buildTree(describeAsJSON());
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

	public void writeToWithErrors(Sheet pane) {
		writeTo(pane);

		for (var err : viewedNode.errors()) {
			pane.appendToCurrentLine(err.msg, g.translator).setForeground(Color.red);
			pane.newLine();
		}
	}

}
