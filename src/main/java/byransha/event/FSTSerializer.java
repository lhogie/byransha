package byransha.event;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.nustaq.serialization.FSTConfiguration;

import byransha.graph.BGraph;

public class FSTSerializer implements EventQueueSerializer {

	private static final ThreadLocal<FSTConfiguration> conf = ThreadLocal
			.withInitial(FSTConfiguration::createDefaultConfiguration);

	@Override
	public void write(InMemoryEventList q, File f) throws IOException {
		var os = new BufferedOutputStream(new FileOutputStream(f));
		conf.get().encodeToStream(os, q);
		os.close();
	}

	@Override
	public InMemoryEventList read(File f, BGraph g) throws Exception {
		var is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
		var q = (InMemoryEventList) conf.get().decodeFromStream(is);
		is.close();
		return q;
	}

	@Override
	public String ext() {
		return "fst";
	}
}
