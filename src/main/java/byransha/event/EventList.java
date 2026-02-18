package byransha.event;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public abstract class EventList extends BNode {
	protected LocalDateTime currentDate = LocalDateTime.now();

	public EventList(BBGraph g) throws IOException {
		super(g);
	}

	public abstract void add(Event e) throws IOException, ClassNotFoundException;

	public abstract Event forward() throws Throwable;

	public abstract Event rewind() throws Throwable;

	@Override
	public String prettyName() {
		return "event list";
	}

	public void goToNow( Consumer<Event> c) throws Throwable {
		goTo(LocalDateTime.now(), c);
	}

	public void goTo(LocalDateTime target, Consumer<Event> c) throws Throwable {
		if (target.isAfter(currentDate)) {
			for (var e = forward(); e != null && e.date.isBefore(target);) {
				e.apply(g);
				currentDate = e.date;
				c.accept(e);
			}
		} else if (target.isBefore(currentDate)) {
			for (var e = rewind(); e != null && e.date.isAfter(target);) {
				e.apply(g);
				currentDate = e.date;
				c.accept(e);
			}
		}
	}
}
