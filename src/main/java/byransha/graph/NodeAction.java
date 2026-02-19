package byransha.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.action.ActionResult;
import byransha.graph.action.DeleteAction;
import byransha.graph.action.ResetNodeAction;
import byransha.graph.action.SearchAction;
import byransha.graph.action.SearchRegexpAction;
import byransha.graph.action.SearchTextAction;
import byransha.graph.action.exportNodeAction;
import byransha.nodes.primitive.FileNode;
import byransha.nodes.primitive.FileNode.openFile;
import byransha.nodes.system.User;
import toools.io.Cout;

public abstract class NodeAction<T extends BNode, R extends BNode> extends BNode {
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
		return getClass().getSimpleName().replaceAll("(?<=[a-z])(?=[A-Z])", " ");
	}

	public abstract String whatItDoes();

	public abstract ActionResult<T, R> exec(T target) throws Throwable;

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
		add(BNode.class, exportNodeAction.class);
		add(BNode.class, ResetNodeAction.class);
		add(BNode.class, DeleteAction.class);
		add(BNode.class, SearchAction.class);
		add(BNode.class, SearchTextAction.class);
		add(BNode.class, SearchRegexpAction.class);
		add(NodeAction.class, NodeAction.exec.class);
		add(FileNode.class, openFile.class);
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
}
