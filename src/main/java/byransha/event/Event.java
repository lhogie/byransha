package byransha.event;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.network.Peer;
import byransha.service.system.Hub;

public abstract class Event extends Element implements Externalizable, Comparable<Event> {
	@ShowInKishanView
	LocalDateTime date;

	@ShowInKishanView
	String problem;

	Set<Peer> owners = new HashSet<>();

	public Event(Element parent) {
		this(parent, LocalDateTime.now());
	}

	public Event(Element parent, LocalDateTime date) {
		super(parent, ID.fromDate(date));
		this.date = date;
	}

	public abstract void apply(Hub g) throws Throwable;

	public abstract void undo(Hub g) throws Throwable;

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
		return date == ((Event) e).date;
	}

	public static final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	public void markReceivedBy(Peer from) {
		owners.add(from);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(date);
		out.writeInt(owners.size());

		for (var o : owners) {
			out.writeObject(o.id());
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		date = (LocalDateTime) in.readObject();
		var ownersSize = in.readInt();

		for (int i = 0; i < ownersSize; i++) {
			UUID ownerId = new UUID(in.readLong(), in.readLong());
			var owner = hub().network.neighborhood.findPeer(ownerId);
			owners.add(owner);
		}
	}

}
