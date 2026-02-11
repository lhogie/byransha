package byransha.event;

import java.io.*;

public  class EventListOneFilePerDay extends EventList {
	public EventListOneFilePerDay(File directory) throws IOException {
		super(directory);
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



}
