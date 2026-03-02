package byransha.graph.view;

import java.io.IOException;
import java.util.function.Consumer;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import butils.ByUtils;
import byransha.graph.BBGraph;
import byransha.graph.BNode;

public abstract class NodeView<N extends BNode> extends BNode {
	public final N node;

	public NodeView(BBGraph g, N node) {
		super(g);
		this.node = node;
	}

	public final JsonNode toJSON() {
		return toJSON(node);
	}

	public abstract JsonNode toJSON(N n);

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

	public abstract void createSwingComponents(Consumer<JComponent> c) throws IOException;

}
