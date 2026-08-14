package byransha.thread;

import byransha.Element;
import byransha.action.base.ShowInKishanView;

public class ThreadNode extends Element {

	public final Thread thread;

	public ThreadNode(Element parent, String description, Runnable r) {
		super(parent, null);
		this.thread = thread(description, r);
		// hub().threads.elements.add(this);
	}

	@ShowInKishanView
	public String name() {
		return thread.getName();
	}

	public static final Thread thread(String description, Runnable r) {
		var t = new Thread(() -> {
			try {
				r.run();
			} catch (Throwable err) {
				System.err.println("error in thread " + description);
				err.printStackTrace();
			}
		});
		t.setDaemon(true);
		t.start();
		return t;
	}
}
