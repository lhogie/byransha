package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;

public class ActionResult<T extends BNode, R extends BNode> extends BNode {
	static {
//		NodeAction.actions.put(ActionResult
	}
	public long startDateMs;
	public R result;
	public NodeAction<T, R> runningAction;

	public ActionResult(BBGraph g, NodeAction<T, R> runningAction, R result) {
		super(g);
		this.runningAction = runningAction;
		this.result = result;
	}

	public long durationMs() {
		return System.currentTimeMillis() - startDateMs;
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
			return "stop the running action";
		}

		@Override
		protected ActionResult exec(ActionResult r) throws Throwable {
			r.runningAction.stopRequest = true;
			return null;
		}

		@Override
		public String prettyName() {
			return "Stop";
		}
	}
}
