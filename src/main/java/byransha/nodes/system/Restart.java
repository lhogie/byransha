package byransha.nodes.system;

import byransha.graph.Category;
import byransha.graph.ProcedureAction;

final class Restart extends ProcedureAction<Byransha> {
	public static class byransha extends Category {
	}

	public Restart(Byransha inputNode) {
		super(inputNode, byransha.class);
	}

	@Override
	public String whatItDoes() {
		return "restart";
	}

	@Override
	public void impl() throws Throwable {
		for (int s = 5; s >= 0 && !stopRequested; --s) {
			inputNode.currentVersionNode.set("restart in " + s + "s");
			Thread.sleep(1);
		}

		if (!stopRequested) {
			System.exit(1); // 1 means restart is required
		}
	}

	@Override
	public boolean applies() {
		return true;
	}
}