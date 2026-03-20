package byransha.nodes.system;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;

final class Restart extends NodeAction<Byransha, Byransha> {
	Restart(BGraph g, Byransha inputNode) {
		super(g, inputNode);
	}

	@Override
	public String whatItDoes() {
		return "restarts Byransha";
	}

	@Override
	public ActionResult<Byransha, Byransha> exec(ChatNode chat) throws Throwable {
		for (int s = 5; s >= 0 && !stopRequested; --s) {
			inputNode.versionNode.set("restart in " + s + "s");
			Thread.sleep(1);
		}

		if (!stopRequested) {
			System.exit(1); // 1 means restart is required
		}

		return createResultNode(inputNode, false);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}
}