package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;

public class ActionResult<T extends BNode, R extends BNode> extends BNode {
	static {
//		NodeAction.actions.put(ActionResult
	}
	public long durationMs;
	public R result;
	public final NodeAction<T, R> runningAction;

	public ActionResult(BBGraph g, NodeAction<T, R> runningAction, R result) {
		super(g);
		this.runningAction = runningAction;
		this.result = result;
	}

	public long durationMs() {
		return durationMs;
	}

	@Override
	public String whatIsThis() {
		return "a result of a given action";
	}

	@Override
	public String prettyName() {
		return "result for action " + runningAction;
	}

	public static class stop extends NodeAction<ActionResult, NodeAction> {
		protected stop(BBGraph g, ActionResult r) {
			super(g, r);
		}

		@Override
		public String whatItDoes() {
			return "stops the running action";
		}

		@Override
		public ActionResult exec() {
			inputNode.runningAction.stopRequested = true;
			return createResultNode(inputNode.runningAction);
		}
	}
}
