package byransha.graph.view;

import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class ErrorsView extends NodeView<BNode> {

	public ErrorsView(BBGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "errors";
	}

	@Override
	public JsonNode toJSON(BNode n) {
		ArrayNode r = new ArrayNode(factory);

		for (var err : n.errors()) {
			r.add(err.msg);
		}

		return r;
	}

	@Override
	protected boolean kishanable() {
		return true;
	}

	@Override
	public void createSwingComponents(Consumer<JComponent> onComponentCreated) {
		for (var err : node.errors()) {
			onComponentCreated.accept(new JLabel(err.msg));
		}
	}

	public boolean showInViewList() {
		return true;
	}

	@Override
	protected boolean allowsEditing() {
		return false;
	}
}