package byransha;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class Byransha extends BNode {

	public Byransha(BBGraph g, User creator) {
		super(g, creator, InstantiationInfo.notPersisting);
		endOfConstructor();
	}

	@Override
	protected void createOuts(User creator) {
		super.createOuts(creator);
	}

	@Override
	public String prettyName() {
		return "Byransha";
	}

	@Override
	public String whatIsThis() {
		return "Byransha";
	}

	public static final String VERSION = "0.0.1";

	interface JSONable {
		JsonNode toJson();
	}

	public static class Distribution<E extends Comparable<E>> extends CoupleList<E, Double> {

		public void addOccurence(E a) {
			var e = getEntry(a);

			if (e == null) {
				e = addXY(a, 0d);
			}

			e.y = e.y + 1;
		}
	}

	public static class Function extends CoupleList<Double, Double> {
	}

	public static class CoupleList<X extends Comparable<X>, Y> implements JSONable {
		static class Couple<X extends Comparable<X>, Y> implements JSONable, Comparable<Couple<X, Y>> {
			final X x;
			public Y y;

			public Couple(X x, Y y) {
				this.x = x;
				this.y = y;
			}

			@Override
			public JsonNode toJson() {
				var n = new ObjectNode(null);
				n.set(x.toString(), new TextNode(y.toString()));
				return n;
			}

			@Override
			public int compareTo(Couple<X, Y> o) {
				return x.compareTo(o.x);
			}
		}

		final List<Couple<X, Y>> entries = new ArrayList<>();

		@Override
		public JsonNode toJson() {
			Collections.sort(entries);
			var n = new ArrayNode(null);
			entries.forEach(e -> n.add(e.toJson()));
			return n;
		}

		public Couple<X, Y> getEntry(X x) {
			for (var e : entries) {
				if (e.x.equals(x)) {
					return e;
				}
			}

			return null;
		}

		public Couple<X, Y> addXY(X x, Y y) {
			if (getEntry(x) != null)
				throw new IllegalStateException(x + " was already defined");

			var e = new Couple<>(x, y);
			entries.add(e);
			return e;
		}

	}

}
