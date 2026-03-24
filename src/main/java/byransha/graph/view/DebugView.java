package byransha.graph.view;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class DebugView extends NodeView<BNode> {

	public DebugView(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "technical information";
	}

	
	@Override
	protected boolean allowsEditing() {
		return false;
	}

}