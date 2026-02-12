package byransha.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public abstract class EventList extends BNode {
	public static final File defaultEventListDirectory = new File(System.getProperty("user.home") + "/.byransha");

	public final File directory;
	EventQueueSerializer qFormat = new SerQueueSerializer();

	public EventList(BBGraph g, File directory) throws IOException {
		super(g, g.systemUser);
		this.directory = directory;
		this.directory.mkdirs();
		var is = new ObjectInputStream(new FileInputStream(new File(this.directory, "events")));
		is.close();
	}

	public void add(Event e) throws IOException, ClassNotFoundException {
		File f = getFile(e);
		var q = qFormat.read(new FileInputStream(f));
		q.add(e);
		qFormat.write(q, new FileOutputStream(f));
	}

	public abstract Event forward();

	public abstract Event rewind();

	public abstract File getFile(Event e);
}
