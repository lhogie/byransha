package byransha.event;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public abstract class EventList extends BNode {

	protected LocalDateTime currentDate = LocalDateTime.of(0, 1, 1, 0, 0);

	public EventList(BBGraph g) {
		super(g);
	}

	public abstract void add(Event e)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

	public abstract Event forward() throws Throwable;

	public abstract Event rewind() throws Throwable;

	@Override
	public String prettyName() {
		return "event list";
	}

	public void goToNow(Consumer<Event> c) throws Throwable {
		goTo(LocalDateTime.now(), c);
	}

	public void goTo(LocalDateTime target, Consumer<Event> c) throws Throwable {
		if (target.isAfter(currentDate)) {
			for (var e = forward(); e != null && currentDate.isBefore(target); e = forward()) {
				c.accept(e);
			}
		} else if (target.isBefore(currentDate)) {
			for (var e = rewind(); e != null && currentDate.isAfter(target); e = rewind()) {
				c.accept(e);
			}
		} else {
			throw new IllegalStateException();
		}
	}
}
