package byransha.event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import byransha.graph.BBGraph;

public class EventListOneFilePerDay extends OnDiskEventList {
	public EventListOneFilePerDay(BBGraph g, File directory) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super(g, directory);
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
	public String whatIsThis() {
		return "an event list storing events by day";
	}



	@Override
	protected List<Event> loadQ()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, FileNotFoundException, IOException {
		return null;
	}


}
