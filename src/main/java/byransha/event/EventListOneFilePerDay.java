package byransha.event;

import java.io.File;
import java.io.IOException;

import byransha.graph.BBGraph;

public class EventListOneFilePerDay extends EventList {
	public EventListOneFilePerDay(BBGraph g, File directory) throws IOException {
		super(g, directory);
	}

	@Override
	public Event forward() {
		return null;
	}

	@Override
	public Event rewind() {
		return null;
	}

	@Override
	public File getFile(Event e) {
		return new File(e.date.getYear() + "/" + e.date.getMonthValue(), e.date.getDayOfMonth() + ".ser");
	}

	@Override
	public String whatIsThis() {
		return "an event list storing events by day";
	}

	@Override
	public String prettyName() {
		return "event list";
	}

}
