package byransha.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonQueueSerializer implements EventQueueSerializer {
	public final static ObjectMapper mapper = new ObjectMapper();

	@Override
	public void write(List<Event> q, OutputStream out) throws IOException {
		mapper.writeValue(out, q);
		out.close();
	}

	@Override
	public List<Event> read(InputStream in) throws IOException, ClassNotFoundException {
		var q = (ArrayList<Event>) mapper.readValue(in, ArrayList.class);
		in.close();
		return q;
	}

	@Override
	public String ext() {
		return "json";
	}
}
