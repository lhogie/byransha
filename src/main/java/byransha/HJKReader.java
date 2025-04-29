package byransha;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import toools.reflect.Clazz;
import toools.text.csv.CSV;

public class HJKReader {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.US);

	public ArrayList<Event<?>> read(String s, Map<String, BNode> nme_node)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ParseException {

		var matrix = CSV.disassemble(s, ",");

		// System.out.println(matrix);
		var events = new ArrayList<Event<?>>();

		for (var line : matrix) {
			var className = line.get(0);
			var eventClass = Clazz.findClass(className);

			if (eventClass == null)
				throw new IllegalStateException("dunno event type: " + className);

			var node = nme_node.get(1);
			var event = (Event) eventClass.getConstructor(BNode.class).newInstance(node);
			event.date = dateFormat.parse(line.remove(0)).toInstant();
		//	event.initFromCSV(line.stream().map(e -> e.trim()).toList(), node);
			events.add(event);
		}

		return events;
	}
}