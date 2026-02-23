package byransha.graph.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import butils.ByUtils;
import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.IntNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.ListNode.ElementView;
import byransha.nodes.primitive.StringNode;

public abstract class NodeView<N extends BNode> extends BNode {
	public NodeView(BBGraph g) {
		super(g);
	}

	public String name() {
		return getClass().getSimpleName().toLowerCase();
	}

	public abstract JsonNode toJSON(N n);

	@Override
	public ObjectNode toJSONNode() {
		var r = super.toJSONNode();
		r.put("showInMainWindow", showInViewList());
		r.put("whatItShows", whatItShows());
		return r;
	}

	public final JComponent createComponent(N n) {
		try {
			var c = createComponentImpl(n);
//			c.setPreferredSize(new Dimension(500, 200));
			return c;
		} catch (Throwable err) {
			g.systemNode.errorLog.add(err);
			err.printStackTrace();
			return new JLabel("can't create list renderer for " + n);
		}
	}

	public boolean showInViewList() {
		return true;
	}

	public abstract JComponent createComponentImpl(N n) throws Throwable;

	@Override
	public String whatIsThis() {
		return "a view showing " + whatItShows();
	}

	public abstract String whatItShows();

	@Override
	public String prettyName() {
		return ByUtils.camelToWords(getClass().getSimpleName()).replaceAll(" view", "");
	}

	public static final Map<Class, List<Class>> views = new HashMap<>();

	public static void add(Class c, Class v) {
		var l = views.get(c);

		if (l == null) {
			views.put(c, l = new ArrayList<>());
		}

		l.add(v);
	}

	static {
//		add(BNode.class, AllView.class);
		add(ListNode.class, ElementView.class);
		add(IntNode.class, IntNode.IntNodeView.class);
		add(BooleanNode.class, BooleanNodeView.class);
		add(StringNode.class, StringNodeView.class);
		add(BNode.class, KishanView.class);
		add(BNode.class, SmallInfoView.class);
		add(BNode.class, JumpTo.class);
		add(BNode.class, OutNavigationView.class);
		add(BNode.class, InNavigationView.class);
		add(BNode.class, ErrorsView.class);
		add(BNode.class, AvailableActionsView.class);
		add(BNode.class, DebugView.class);
	}

	protected abstract boolean allowsEditing();

	protected boolean kishanable() {
		return false;
	}

}
