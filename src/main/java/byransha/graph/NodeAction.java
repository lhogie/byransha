package byransha.graph;

import java.awt.Color;

import com.fasterxml.jackson.databind.node.ObjectNode;

import butils.ByUtils;
import butils.Cout;
import byransha.graph.action.ActionResult;
import byransha.nodes.system.User;

public abstract class NodeAction<IN extends BNode, OUT extends BNode> extends BNode {
	@Hide
	protected final IN inputNode;
	public boolean stopRequested = false;
	public Thread thread;
	public boolean execStraightAway = false;

	public NodeAction(BGraph g, IN inputNode) {
		super(g);
		this.inputNode = inputNode;
	}

	@Override
	public void createActions() {
		cachedActions.add(new exec<OUT>(g, this));
		super.createActions();
	}

	@Override
	public ObjectNode toJSONNode() {
		var r = super.toJSONNode();
		r.put("canExecute", canExecute(currentUser()));
		r.put("whatItDoes", whatItDoes());
		return r;
	}

	protected final ActionResult<IN, OUT> createResultNode(OUT out, boolean jumpStraightAwayToResult) {
		return new ActionResult<IN, OUT>(g, this, out, jumpStraightAwayToResult);
	}

	public boolean canExecute(User user) {
		return true;
	}

	public boolean wantToBeProposedFor(BNode bNode) {
		return true;
	}

	@Override
	public String prettyName() {
		return ByUtils.camelToWords(getClass().getSimpleName()).replaceAll(" view", "");
	}

	public String technicalName() {
		return prettyName().replace(' ', '_').toLowerCase();
	}

	public abstract String whatItDoes();

	public abstract ActionResult<IN, OUT> exec() throws Throwable;

	@Override
	public Color getColor() {
		return Color.white;
	}

	@Override
	public final String whatIsThis() {
		return "an action which " + whatItDoes();
	}

	static public class exec<OUT extends BNode> extends NodeAction<NodeAction, OUT> {

		public exec(BGraph g, NodeAction inputNode) {
			super(g, inputNode);
			execStraightAway = true;
		}

		@Override
		public String whatItDoes() {
			return "trigger the action on the source node";
		}

		boolean execStraight() {
			return getClass().getEnclosingClass() == NodeAction.class;
		}

		@Override
		public ActionResult<NodeAction, OUT> exec() throws Throwable {
			Cout.debugSuperVisible("exec " + inputNode.prettyName());
			var startDateMs = System.currentTimeMillis();
			var r = inputNode.exec();
			r.durationMs.set(System.currentTimeMillis() - startDateMs);
			return r;
		}

		@Override
		public String prettyName() {
			return "Run";
		}

		@Override
		public boolean applies() {
			return true;
		}

	}

	public abstract boolean applies();

}
