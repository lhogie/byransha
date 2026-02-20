package byransha.graph.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.IntNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.ListNode.ElementView;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.TextNode;
import byransha.nodes.primitive.TextNode.saveNodeAction;

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
		var c = createComponentImpl(n);
		return c;
	}

	public boolean showInViewList() {
		return true;
	}

	public abstract JComponent createComponentImpl(N n);

	@Override
	public String whatIsThis() {
		return "a view showing " + whatItShows();
	}

	public abstract String whatItShows();

	@Override
	public String prettyName() {
		return getClass().getSimpleName();
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
		add(BNode.class, SmallInfoView.class);
		add(BNode.class, JumpToView.class);
		add(BNode.class, OutNavigationView.class);
		add(BNode.class, InNavigationView.class);
		add(BNode.class, HistoryView.class);
		add(BNode.class, ErrorsView.class);
		add(BNode.class, AvailableActionsView.class);
		add(BNode.class, KishanView.class);
		add(BNode.class, DebugView.class);
		add(ListNode.class, ElementView.class);
		add(IntNode.class, IntNode.IntNodeView.class);
		add(BooleanNode.class, BooleanNodeView.class);
		add(StringNode.class, StringNodeView.class);
		add(TextNode.class, saveNodeAction.class);
	}

	protected abstract boolean allowsEditing();

	protected boolean kishanable() {
		return false;
	}

}
