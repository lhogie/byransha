package byransha.event;

import java.io.File;
import java.io.IOException;

import byransha.ID;
import byransha.graph.Element;

public class SingleFileEventList extends InMemoryEventList {
	final File f;
	protected EventQueueSerializer ser = new FSTEventQueueSerializer();

	public SingleFileEventList(Element g, File f) {
		super(g);
		this.f = f;
	}

	@Override
	public void add(Event e) {
		System.out.println("adding event " + e);
		super.add(e);
		try {
			ser.write(this, f);
		} catch (IOException e1) {
			hub().errorLog.add(e1);
		}
	}

	@Override
	public Event remove(ID id) throws IOException {
		var e = super.remove(id);
		ser.write(this, f);
		return e;

	}

	@Override
	public String toString() {
		return whatIsThis();
	}

	@Override
	public String whatIsThis() {
		return "single file: " + f.getPath();
	}
}
