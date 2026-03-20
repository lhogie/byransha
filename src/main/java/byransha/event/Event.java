package byransha.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import byransha.graph.BGraph;
import byransha.network.PeerNode;

public abstract class Event implements Serializable, Comparable<Event> {
	final LocalDateTime date;
	Set<PeerNode> owners = new HashSet<>();
	public long ID;
	final protected BGraph g;

	public Event(BGraph g, LocalDateTime date) {
		this.date = date;
		this.g = g;
	}

	public abstract void apply(BGraph g) throws Throwable;;

	public abstract void undo(BGraph g) throws Throwable;

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

	public void markReceivedBy(PeerNode from) {
		owners.add(from);
	}

	public void commitToDisk() {
	}

	protected abstract void fromCSV(Iterator<String> elementIterator, BGraph g) throws ClassNotFoundException;

}
