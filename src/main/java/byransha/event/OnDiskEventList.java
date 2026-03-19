package byransha.event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import byransha.graph.BGraph;

public abstract class OnDiskEventList extends EventList {
	protected EventQueueSerializer qFormat = new CSVSerializer();
	public final File directory;

	public OnDiskEventList(BGraph g, File directory)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super(g);
		this.directory = directory;

		this.directory.mkdirs();
	}

	protected abstract List<Event> loadQ()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, FileNotFoundException, IOException;

	@Override
	public void add(Event e) {
		try {
			var f = getFile(e);
			var fos = new FileOutputStream(f);
//		qFormat.write(q, fos);
			fos.close();
		} catch (IOException err) {
			error(err);
		}
	}

	public File getFile(Event e) {
		return new File(directory,
				e.date.getYear() + "/" + e.date.getMonthValue() + "/" + e.date.getDayOfMonth() + ".ser");
	}
}
