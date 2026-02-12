package byransha.event;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import byransha.graph.BBGraph;

public abstract class Event implements Serializable, Comparable<Event> {
	LocalDateTime date;

	public abstract void apply(BBGraph g);

	public abstract void undo(BBGraph g);

	@Override
	public int compareTo(Event e) {
		return date.compareTo(e.date);
	}

	@Override
	public int hashCode() {
		return date.hashCode();
	}

	@Override
	public boolean equals(Object e) {
		if (!(e instanceof Event)) {
			return false;
		}
		return date == ((Event) e).date;
	}

	public static final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	public final List<String> toCSVElements() {
		var l = new ArrayList<String>();
		l.add(date.format(dateFormat));
		fillCSVColumns(l);
		return l;
	}

	protected abstract void fillCSVColumns(ArrayList<String> l);

	protected abstract void fromCSV(List<String> elements);

	protected static Event fromCSVElements(List<String> elements)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException {
		var e = (Event) Class.forName(elements.removeFirst()).getConstructor().newInstance();
		e.date = LocalDateTime.parse(elements.removeFirst(), dateFormat);
		e.fromCSV(elements);
		return e;
	}
}
