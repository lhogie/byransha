package byransha.event;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Predicate;

public abstract class EventList {
	final File directory;
	PriorityQueue<Event> q;

	public EventList(File directory) throws IOException {
		this.directory = directory;
		this.directory.mkdirs();
		var is = new ObjectInputStream(new FileInputStream(new File(this.directory, "events")));
		q = (PriorityQueue<Event>) is.readObject();
		is.close();

	}

	public void add(Event e) throws IOException, ClassNotFoundException {
		File f = getFile(e);
		var q = readQueue(f);
		q.add(e);
		writeQueue(f, q);
	}

	private  PriorityQueue readQueue(File f) throws IOException, ClassNotFoundException {
		var is = new ObjectInputStream(new FileInputStream(f));
		q = (PriorityQueue) is.readObject();
		is.close();
		return q;
	}

	private void writeQueue(File f, PriorityQueue<Event> q) throws IOException {
		var os = new ObjectOutputStream(new FileOutputStream(f));
		os.writeObject(q);
		os.close();
	}

	public abstract Event forward();
	public abstract Event rewind();

	public abstract File getFile(Event e);
}
