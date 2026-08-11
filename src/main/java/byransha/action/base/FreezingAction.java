package byransha.action.base;

import byransha.Element;
import byransha.action.Action;
import byransha.action.Category;

public final class FreezingAction extends Action {
	public FreezingAction(Element g) {
		super(g, misc.class);
	}

	public static class misc extends Category {
	}

	@Override
	public boolean wantToBeProposedFor(Element n) {
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