package byransha.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import byransha.graph.BGraph;

public class SerQueueSerializer implements EventQueueSerializer {

	@Override
	public void write(List<Event> q, OutputStream out) throws IOException {
		var os = new ObjectOutputStream(out);
		os.writeObject(q);
		os.close();
	}

	@Override
	public List<Event> read(InputStream in, BGraph g) throws IOException, ClassNotFoundException {
		var is = new ObjectInputStream(in);
		var q = (List<Event>) is.readObject();
		is.close();
		return q;
	}

	@Override
	public String ext() {
		return "ser";
	}
}
