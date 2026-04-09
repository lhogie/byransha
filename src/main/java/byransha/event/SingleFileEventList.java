package byransha.event;

import java.io.File;
import java.io.IOException;

import byransha.graph.BGraph;

public class SingleFileEventList extends InMemoryEventList {
	final File f;
	protected EventQueueSerializer ser = new FSTSerializer();

	public SingleFileEventList(BGraph g, File f) {
		super(g);
		this.f = f;
	}

	@Override
	public void add(Event e) throws IOException {
		super.add(e);
		ser.write(this, f);
	}

	@Override
	public Event remove(long id) throws IOException {
		var e = super.remove(id);
		ser.write(this, f);
		return e;

	}

	@Override
	public String whatIsThis() {
		return "an event-list backed up in a file";
	}
}
