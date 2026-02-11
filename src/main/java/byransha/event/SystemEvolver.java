package byransha.event;

import byransha.nodes.BNode;

import java.util.ArrayList;
import java.util.List;

public class SystemEvolver<S extends BNode> {
	public final List<Event<S>> events = new ArrayList<>();
	public final S system;
	int cursor = 0;

	public SystemEvolver(S s) {
		this.system = s;
	}

	public void forward() {
		if (cursor >= events.size())
			throw new IllegalStateException("no further ever");

		var e = events.get(cursor++);
		e.apply(system);
	}

	public void backward() {
		if (cursor <= 0)
			throw new IllegalStateException("no previous ever");

		var e = events.get(--cursor);
		e.undo(system);
	}
}
