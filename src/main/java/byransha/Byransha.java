package byransha;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class Byransha extends BNode {
	public Byransha(BBGraph db) {
		super(db);
	}

	public Byransha(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public String prettyName() {
		return "Byransha";
	}

	@Override
	public String whatIsThis() {
		return "Byransha Node Description";
	}

	public static final String VERSION = "0.0.1";

	interface JSONable {
		JsonNode toJson();
	}

	public static class Distribution<E> extends XY<E, Double> {
		public void addOccurence(E a) {
			var e = getEntry(a);

			if (e == null) {
				e = addXY(a, 0d);
			}

			e.y = e.y + 1;
		}
	}

	public static class Function extends XY<Double, Double> {
	}

	public static class XY<X, Y> implements JSONable {
		static class Entry<X, Y> implements JSONable {
			final X x;
			public Y y;

			public Entry(X x, Y y) {
				this.x = x;
				this.y = y;
			}

			@Override
			public JsonNode toJson() {
				var n = new ObjectNode(null);
				n.set(x.toString(), new TextNode(y.toString()));
				return n;
			}
		}

		final List<Entry<X, Y>> entries = new ArrayList<>();

		@Override
		public JsonNode toJson() {
			var n = new ArrayNode(null);
			entries.forEach(e -> n.add(e.toJson()));
			return n;
		}

		public Entry<X, Y> getEntry(X x) {
			for (var e : entries) {
				if (e.x.equals(x)) {
					return e;
				}
			}

			return null;
		}

		public Entry<X, Y> addXY(X x, Y y) {
			var e = new Entry<>(x, y);
			entries.add(e);
			return e;
		}

	}

}
