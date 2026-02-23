package byransha.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.action.ActionResult;
import byransha.graph.action.Delete;
import byransha.graph.action.Reset;
import byransha.graph.action.Export;
import byransha.graph.action.search.Search;
import byransha.graph.action.search.SearchRegexp;
import byransha.graph.action.search.SearchText;
import byransha.nodes.primitive.FileNode;
import byransha.nodes.primitive.FileNode.openFile;
import byransha.nodes.primitive.TextNode;
import byransha.nodes.primitive.TextNode.saveNodeAction;
import byransha.nodes.system.User;
import toools.io.Cout;

public abstract class NodeAction<IN extends BNode, OUT extends BNode> extends BNode {
	public boolean stopRequest;

	public NodeAction(BBGraph g) {
		super(g);
	}

	@Override
	public ObjectNode toJSONNode() {
		var r = super.toJSONNode();
		r.put("canExecute", canExecute(currentUser()));
		r.put("whatItDoes", whatItDoes());
		return r;
	}

	public boolean canExecute(User user) {
		return true;
	}

	public boolean wantToBeProposedFor(BNode bNode) {
		return true;
	}

	@Override
	public String prettyName() {
		// camel case to words
		return getClass().getSimpleName().replaceAll("(?<=[a-z])(?=[A-Z])", " ");
	}

	public abstract String whatItDoes();

	public abstract ActionResult<IN, OUT> exec(IN target) throws Throwable;

	@Override
	public Color getColor() {
		return Color.white;
	}

	@Override
	public String whatIsThis() {
		return "an action which " + whatItDoes();
	}

	public static final Map<Class, List<Class>> actions = new HashMap<>();

	public static void add(Class c, Class v) {
		var l = actions.get(c);

		if (l == null) {
			actions.put(c, l = new ArrayList<>());
		}

		l.add(v);
	}

	static {
		add(BNode.class, Export.class);
		add(BNode.class, Reset.class);
		add(BNode.class, Delete.class);
		add(BNode.class, Search.class);
		add(BNode.class, SearchText.class);
		add(BNode.class, SearchRegexp.class);
		add(NodeAction.class, NodeAction.exec.class);
		add(FileNode.class, openFile.class);
		add(TextNode.class, saveNodeAction.class);
	}

	static public class exec extends NodeAction<NodeAction, BNode> {

		public exec(BBGraph g) {
			super(g);
		}

		@Override
		public String whatItDoes() {
			return "trigger its action";
		}

		boolean execStraight() {
			return getClass().getEnclosingClass() == NodeAction.class;
		}

		@Override
		public ActionResult exec(NodeAction target) throws Throwable {
			Cout.debugSuperVisible("exec " + target.prettyName());
			var r = target.exec(target);
			r.startDateMs = System.currentTimeMillis();
			return r;
		}

		@Override
		public String prettyName() {
			return "Run";
		}
	}

	public String commandName() {
		var s = getClass().getSimpleName().toLowerCase();
		if (s.endsWith("action"))
			s = s.substring(0, s.length() - 6);
		return s;
	}
}
