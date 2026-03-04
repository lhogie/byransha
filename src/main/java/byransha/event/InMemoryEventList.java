package byransha.event;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import byransha.graph.BGraph;

public class InMemoryEventList extends EventList {
	List<Event> q = new ArrayList<>();
	int lastExecutedEventIndex = -1;

	public InMemoryEventList(BGraph g) {
		super(g);
	}

	@Override
	public void add(Event e) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		q.add(e);
		Collections.sort(q);
	}

	@Override
	public Event forward() throws Throwable {
		if (lastExecutedEventIndex < q.size() - 1) {
			var e = q.get(++lastExecutedEventIndex);
			e.apply(g);
			currentDate = e.date;
			return e;
		} else {
			return null;
		}
	}

	@Override
	public Event rewind() throws Throwable {
		if (lastExecutedEventIndex >= 0) {
			var e = q.get(lastExecutedEventIndex--);
			e.undo(g);
			currentDate = e.date;
			return e;
		} else {
			return null;
		}
	}

	@Override
	public String whatIsThis() {
		return "an event-list in RAM";
	}
}
