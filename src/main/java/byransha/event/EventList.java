package byransha.event;

import java.io.IOException;
import java.time.LocalDateTime;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public abstract class EventList extends BNode {
	protected LocalDateTime currentDate;

	public EventList(BBGraph g) throws IOException {
		super(g, g.systemUser);
	}

	public abstract void add(Event e) throws IOException, ClassNotFoundException;

	public abstract Event forward() throws Throwable;

	public abstract Event rewind() throws Throwable;

	@Override
	public String prettyName() {
		return "event list";
	}

	public void goToNow() throws Throwable {
		goTo(LocalDateTime.now());
	}

	public void goTo(LocalDateTime target) throws Throwable {
		if (target.isAfter(currentDate)) {
			for (var e = forward(); e.date.isBefore(target);) {
				e.apply(g);
				currentDate = e.date;
			}
		} else if (target.isBefore(currentDate)) {
			for (var e = rewind(); e.date.isAfter(target);) {
				e.apply(g);
				currentDate = e.date;
			}
		}
	}
}
