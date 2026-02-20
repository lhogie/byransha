package byransha.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface EventQueueSerializer {
	void write(List<Event> q, OutputStream out) throws IOException;

	List<Event> read(InputStream in)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

	String ext();
}
