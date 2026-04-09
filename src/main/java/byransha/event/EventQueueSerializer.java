package byransha.event;

import java.io.File;
import java.io.IOException;

import byransha.graph.BGraph;

public interface EventQueueSerializer {
	void write(InMemoryEventList q, File out) throws IOException;

	InMemoryEventList read(File in, BGraph g) throws Exception;

	String ext();
}
