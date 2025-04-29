package byransha;

import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Predicate;

public abstract class EventList extends PriorityQueue<Event> {
	public Event findByDate(long epoc) {
		return stream().filter(e -> e.date.equals(epoc)).findFirst().orElse(null);
	}

	public Event findFirst(Predicate<Event> p) {
		return stream().filter(p).findFirst().orElse(null);
	}

	public List<Event> findAll(Predicate<Event> p) {
		return stream().filter(p).toList();
	}
}
