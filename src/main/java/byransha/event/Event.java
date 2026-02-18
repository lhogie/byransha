package byransha.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.function.Consumer;

import byransha.graph.BBGraph;

public abstract class Event implements Serializable, Comparable<Event> {
	final LocalDateTime date;

	public Event(LocalDateTime date) {
		this.date = date;
	}

	public abstract void apply(BBGraph g) throws Throwable;;

	public abstract void undo(BBGraph g) throws Throwable;

	@Override
	public int compareTo(Event e) {
		return date.compareTo(e.date);
	}

	@Override
	public int hashCode() {
		return date.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + " at " + date;
	}

	@Override
	public boolean equals(Object e) {
		if (!(e instanceof Event)) {
			return false;
		}
		return date == ((Event) e).date;
	}

	public static final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	public void provideCSVElements(Consumer<String> l) {
		l.accept(getClass().getName());
		l.accept(date.toString());
	}

	public abstract void fromCSV(Iterator<String> l);

}
