package byransha.graph.view;

import java.awt.Dimension;

import javax.swing.JScrollPane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import butils.ByUtils;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.swing.ByranshaUserPane;

public abstract class NodeView<N extends BNode> extends BNode {
	public final N n;

	public NodeView(BGraph g, N node) {
		super(g);
		this.n = node;
	}

	public abstract JsonNode toJSON();

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

	public void writeTo(ByranshaUserPane pane) {
		var c = ByUtils.JsonToTreeConverter.buildTreeModel(toJSON());
		var sp = new JScrollPane(c);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setPreferredSize(new Dimension(500, 100));
		pane.append(sp);
	}

}
