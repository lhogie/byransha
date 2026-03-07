package byransha.graph.view;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ByranshaUserPane;

public class AllView extends NodeView<BNode> {

	public AllView(BGraph g, BNode n) {
		super(g, n);
	}

	@Override
	public String whatItShows() {
		return "all available views";
	}

	@Override
	public JsonNode toJSON() {
		return viewedNode.toJSONNode();
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		try {
			for (var v : viewedNode.views()) {
				if (v.showInViewList()) {
					v.writeTo(pane);
					pane.newLine();
				}
			}
		} catch (Throwable err) {
			err.printStackTrace();
		}
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

	@Override
	public boolean showInViewList() {
		return false;
	}

}