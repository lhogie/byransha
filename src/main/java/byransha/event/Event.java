package byransha.event;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import byransha.graph.BGraph;
import byransha.network.PeerNode;

public abstract class Event implements Externalizable, Comparable<Event> {
	LocalDateTime date;
	Set<PeerNode> owners = new HashSet<>();
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

	public void markReceivedBy(PeerNode from) {
		owners.add(from);
	}

	public long id() {
		return date.toEpochSecond(java.time.ZoneOffset.UTC);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(id());
		out.writeInt(owners.size());

		for (var o : owners) {
			out.writeLong(o.id);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		date = LocalDateTime.ofEpochSecond(in.readLong(), 0, java.time.ZoneOffset.UTC);
		var ownersSize = in.readInt();

		for (int i = 0; i < ownersSize; i++) {
			var ownerId = in.readInt();
			var owner = g.networkAgent.findPeer(ownerId);
			owners.add(owner);
		}
	}

}
