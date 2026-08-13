package byransha.event;

import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import byransha.Element;
import byransha.ID;
import byransha.Service;
import byransha.action.base.ShowInKishanView;
import byransha.network.Message;
import byransha.primitive.StringNode;
import byransha.security.AES;
import byransha.thread.ThreadNode;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

public abstract class EventList extends Service {
	StringNode status;
	protected LocalDateTime currentDate = LocalDateTime.of(0, 1, 1, 0, 0);
	Key encryptionKey = AES.createStringBasedOnHardware();

	@ShowInKishanView
	ThreadNode t = new ThreadNode(this, "event list dissemination thread", () -> {
		while (true) {
			List<Event> candidates = new ArrayList<>();
			status.set("running " + candidates.size() + " event(s) sent");
			forEachEvent(e -> {
				if (e.owners.size() < 1) {
					candidates.add(e);

					for (var neighbor : hub().network.neighborhood.neighbors()) {
						var msg = createNewMessage();
						msg.recipient = neighbor;
						msg.content = e;
						hub().network.sender.accept(msg);
					}

					status.set("running " + candidates.size() + " event(s) sent");
				}
			});

			for (int nbSecPause = 10; nbSecPause > 0; --nbSecPause) {
				status.set(candidates.size() + " event(s) sent. Resend in " + nbSecPause + "s");
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	});

	public EventList(Element parent) {
		super(parent);
		status = new StringNode(parent, null, "", null);

	}

	public LongList collectIDs() {
		var l = new LongArrayList();
		forEachEvent(e -> l.add(e.date.toEpochSecond(java.time.ZoneOffset.UTC)));
		return l;
	}

	protected abstract void forEachEvent(Consumer<Event> c);

	public abstract void add(Event e);

	public abstract Event forward() throws Throwable;

	public abstract Event rewind() throws Throwable;

	@Override
	public String toString() {
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

	public abstract Event findEvent(ID eventID);

	public abstract Event remove(ID id) throws IOException;

	@Override
	protected void incomingMessage(Message msg) {
		Event e = (Event) msg.content;
		var alreadyKnownEvent = hub().eventList.findEvent(e.id());

		if (alreadyKnownEvent == null) {
			hub().eventList.add(e);
		}
	}

}
