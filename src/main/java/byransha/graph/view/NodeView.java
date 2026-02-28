package byransha.graph.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import butils.ByUtils;
import byransha.graph.BBGraph;
import byransha.graph.BNode;

public abstract class NodeView<N extends BNode> extends BNode {
	public static final Map<Class, List<Class>> views = new HashMap<>();

	public static void add(Class c, Class v) {
		var l = views.get(c);

		if (l == null) {
			views.put(c, l = new ArrayList<>());
		}

		l.add(v);
	}

	public final N node;

	public NodeView(BBGraph g, N node) {
		super(g);
		this.node = node;
	}

	public String name() {
		return getClass().getSimpleName().toLowerCase();
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

	protected abstract boolean allowsEditing();

	protected boolean kishanable() {
		return false;
	}

	public abstract void addTo(Consumer<JComponent> c) throws IOException;

}
