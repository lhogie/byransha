package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.LongNode;

public class ActionResult<T extends BNode, R extends BNode> extends BNode {

	public final LongNode durationMs;
	public final R result;
	public final NodeAction<T, R> runningAction;
	public final boolean jumpStraightAwayToResult;

	public ActionResult(BGraph g, NodeAction<T, R> runningAction, R result, boolean jumpStraightAwayToResult) {
		super(g);
		this.runningAction = runningAction;
		this.result = result;
		this.durationMs = new LongNode(g);
		this.jumpStraightAwayToResult = jumpStraightAwayToResult;
	}

	@Override
	public void createActions() {
		cachedActions.add(new stop(g, this));
		super.createActions();
	}

	@Override
	public String whatIsThis() {
		return "a result of a given action";
	}

	@Override
	public String prettyName() {
		return "result for action " + runningAction;
	}

	public static class stop extends NodeAction<ActionResult, ActionResult> {
		protected stop(BGraph g, ActionResult r) {
			super(g, r);
		}

		@Override
		public String whatItDoes() {
			return "stops the running action";
		}

		@Override
		public ActionResult exec() {
			inputNode.runningAction.stopRequested = true;
			return createResultNode(inputNode, false);
		}
	}
}
