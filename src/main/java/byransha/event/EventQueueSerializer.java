package byransha.event;

import java.io.File;
import java.io.IOException;

import byransha.graph.Root;

public interface EventQueueSerializer {
	void write(InMemoryEventList q, File out) throws IOException;

	InMemoryEventList read(File in, Root g) throws Exception;

	String ext();
}
