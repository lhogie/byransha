package byransha.graph.view;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;

import butils.ByUtils;
import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class DebugView extends NodeView<BNode> {

	public DebugView(BBGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "technical information";
	}

	@Override
	public JsonNode toJSON(BNode n) {
		return n.toJSONNode();
	}

	@Override
	public JComponent createComponentImpl(BNode n) {
		return ByUtils.JsonToTreeConverter.buildTreeModel(toJSON(n));
	}

	

	@Override
	protected boolean allowsEditing() {
		return false;
	}

}