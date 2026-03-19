package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.system.ChatNode;

public class ActionResult<T extends BNode, R extends BNode> extends BNode {

	public final LongNode durationMs;
	public final R outNode;
	public final NodeAction<T, R> runningAction;
	public final boolean jumpStraightAwayToOutNode;

	public ActionResult(BGraph g, NodeAction<T, R> runningAction, R result, boolean jumpStraightAwayToResult) {
		super(g);
		this.runningAction = runningAction;
		this.outNode = result;
		this.durationMs = new LongNode(g);
		this.jumpStraightAwayToOutNode = jumpStraightAwayToResult;
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new stop(g, this));
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
		public boolean applies(ChatNode chat) {
			return inputNode.runningAction.thread != null;
		}

		@Override
		public String whatItDoes() {
			return "stops the running action";
		}

		@Override
		public ActionResult exec(ChatNode chat) {
			inputNode.runningAction.stopRequested = true;
			return createResultNode(inputNode, false);
		}
	}
}
