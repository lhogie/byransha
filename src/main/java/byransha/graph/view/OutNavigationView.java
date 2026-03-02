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
	public void createSwingComponents(Consumer<JComponent> onComponentCreated) {
		node.forEachOut((name, out) -> {
			for (var v : out.views()) {
				if (v instanceof JumpTo j) {
					j.createSwingComponents(onComponentCreated);
					return;
				}
			}				
			throw new IllegalStateException(out.getClass() + " has no jump view" );
		});

	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}

}