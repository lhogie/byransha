package byransha.graph.view;

import java.awt.Color;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ChatSheet;
import byransha.ui.swing.Sheet;
import byransha.util.ByUtils;

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
		return ByUtils.JsonToTreeConverter.buildTreeModel(jsonView());
	}

	public void writeTo(ChatSheet pane) {
		byransha.ui.swing.Utils.resizableScrollPane(getJSONDisplayComponent());
	}

	public void writeToWithErrors(ChatSheet pane) {
		writeTo(pane);

		for (var err : viewedNode.errors()) {
			pane.appendToCurrentLine(err.msg, g.translator).setForeground(Color.red);
			pane.newLine();
		}
	}

}
