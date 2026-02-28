package byransha.graph.view;

import java.util.function.Consumer;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class OutNavigationView extends NodeView<BNode> {

	public OutNavigationView(BBGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "OUTs";
	}

	@Override
	public JsonNode toJSON(BNode n) {
		ObjectNode r = new ObjectNode(BNode.factory);

		return r;
	}

	@Override
	public void addTo(Consumer<JComponent> onComponentCreated) {
		node.forEachOut((name, out) -> {
			out.views().stream().filter(v -> v instanceof JumpTo).map(v -> (JumpTo) v).toList().getFirst()
					.addTo(onComponentCreated);
		});
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

}