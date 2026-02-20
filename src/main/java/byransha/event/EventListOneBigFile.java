package byransha.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import byransha.graph.BBGraph;

public class EventListOneBigFile extends OnDiskEventList {
	private final File file;
	int i;

	public EventListOneBigFile(BBGraph g, File directory)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super(g, directory);
		this.file = new File(directory, "events." + qFormat.ext());
	}

	@Override
	protected List<Event> loadQ()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, FileNotFoundException, IOException {
		var f = new File(directory, "events." + qFormat.ext());
		return f.exists() ? qFormat.read(new FileInputStream(f)) : new ArrayList<Event>();
	}

	@Override
	public Event forward() throws Throwable {
		return null;
	}

	@Override
	public Event rewind() {
		return null;
	}

	@Override
	public File getFile(Event e) {
		return file;
	}

	@Override
	public String whatIsThis() {
		return "an event list storing all events in a big file";
	}

}
