package byransha.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class CSVSerializer implements EventQueueSerializer {
	ObjectMapper mapper = new ObjectMapper();

	@Override
	public void write(List<Event> q, OutputStream out) throws IOException {
		var pw = new PrintWriter(new OutputStreamWriter(out));

		q.forEach(event -> {
			var l = new ArrayList<String>();
			event.provideCSVElements(element -> l.add(element));

			for (int i = 0; i < l.size(); ++i) {
				l.set(i, '"' + l.get(i) + '"');
			}

			pw.println(l.stream().collect(Collectors.joining(", ")));
		});

		pw.close();
	}

	private static boolean needProtecting(String s) {
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);

			if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ArrayList<Event> read(InputStream in)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		var q = new ArrayList<Event>();
		var br = new BufferedReader(new InputStreamReader(in));
		String line;

		while ((line = br.readLine()) != null) {
			var json = '[' + line + ']';
			var arrayNode = (ArrayNode) mapper.readTree(json);
			var elementIterator = StreamSupport.stream(arrayNode.spliterator(), false).map(n -> (TextNode) n)
					.map(n -> n.asText()).iterator();
			var className = elementIterator.next();
			LocalDateTime date = LocalDateTime.parse(elementIterator.next());
			Event e = (Event) Class.forName(className).getConstructor(LocalDateTime.class).newInstance(date);
			e.fromCSV(elementIterator);
			q.add(e);
		}

		br.close();
		return q;
	}

	@Override
	public String ext() {
		return "csv";
	}
}
