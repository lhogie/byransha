package byransha.graph.action;

import byransha.graph.Action;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Category;

public final class FreezingAction extends Action {
	public FreezingAction(BGraph g) {
		super(g, misc.class);
	}

	public static class misc extends Category {
	}

	@Override
	public boolean wantToBeProposedFor(BNode n) {
		return true;
	}

	@Override
	public String whatItDoes() {
		return "loops infinitely";
	}

	@Override
	public void impl() throws Throwable {
		new Thread(() -> {
			while (true) {
				if (stopRequested) {
					break;
				}
			}
		}).start();
	}

	@Override
	public boolean applies() {
		return true;
	}
}