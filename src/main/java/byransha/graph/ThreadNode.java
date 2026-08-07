package byransha.graph;

import byransha.system.SystemNode;

public class ThreadNode extends SystemNode {

	public final Thread thread;

	public ThreadNode(BNode parent, String description, Runnable r) {
		super(parent);
		this.thread = thread(description, r);
//		hub().threads.elements.add(this);
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
				err.printStackTrace();
			}
		});
		t.setDaemon(true);
		t.start();
		return t;
	}
}
