package byransha.graph.view;

import java.util.function.Consumer;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class InNavigationView extends NodeView<BNode> {

	public InNavigationView(BBGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "INs";
	}

	@Override
	public JsonNode toJSON(BNode n) {
		ObjectNode r = new ObjectNode(BNode.factory);

		return r;
	}

	@Override
	public void addTo(Consumer<JComponent> onComponentCreated) {
		for (var in : node.computeIns()) {
			for (var v : in.source().views()) {
				if (v instanceof JumpTo jt) {
					jt.addTo(onComponentCreated);
					break;
				}
			}
		}
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

}