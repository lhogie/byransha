package byransha.thread;

import java.util.function.Supplier;

import byransha.Element;
import byransha.util.ByUtils;

public  class LoopingThreadNode extends ThreadNode {
	public LoopingThreadNode(Element parent, Supplier<Double> durationS, String description, Runnable r) {
		super(parent, description, () -> {
			while (true) {
				double wait = durationS.get();

				if (wait > 0) {
					ByUtils.sleep(wait);
				}

				r.run();
			}
		});
	}
}
