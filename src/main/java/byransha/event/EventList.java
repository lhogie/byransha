package byransha.event;

import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.StringNode;
import byransha.security.AES;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

public abstract class EventList extends BNode {
	StringNode status;
	protected LocalDateTime currentDate = LocalDateTime.of(0, 1, 1, 0, 0);
	Key encryptionKey = AES.createStringBasedOnHardware();

	public EventList(BGraph g) {
		super(g);
		status = new StringNode(g);

		new Thread(() -> {
			while (true) {
				List<Event> candidates = new ArrayList<>();
				status.set("running " + candidates.size() + " event(s) sent");
				forEachEvent(e -> {
					if (e.owners.size() < 1) {
						try {
							candidates.add(e);
							g.networkAgent.send(e);
							status.set("running " + candidates.size() + " event(s) sent");
						} catch (IOException err) {

						}
					}
				});

				for (int nbSecPause = 10; nbSecPause > 0; --nbSecPause) {
					status.set(candidates.size() + " event(s) sent. Resend in " + nbSecPause + "s");
					try {
						Thread.currentThread().sleep(1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}, "event list dissemination thread").start();
	}

	public LongList collectIDs() {
		var l = new LongArrayList();
		forEachEvent(e -> l.add(e.ID));
		return l;
	}

	private void forEachEvent(Consumer<Event> c) {
		// TODO Auto-generated method stub

	}

	public abstract void add(Event e);

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

	public Event findEvent(long eventID) {
		return null;
	}
}
