package byransha;

import java.lang.reflect.Field;
import java.util.function.Function;

import byransha.graph.Element;
import byransha.graph.Hub;
import byransha.graph.OutChangedEvent;

public class Out<T extends Element> {
	private T head;
	public final Element tail;
	public final Field field;

	public static interface OutChangeListener<T extends Element> {
		void outChanged(Element e, Out out, T a, T b);
	}

	public Out(Element o, String fieldName, Function<ID, T> instantiator) {
		this.head = lookupOrCreate(o.hub(), o.id().augmentWith(fieldName), instantiator);
		this.tail = o;
		this.field = findField(o.getClass(), fieldName);
	}

	private static Field findField(Class<? extends Element> c, String fieldName) {
		while (c != null) {
			for (var f : c.getDeclaredFields()) {
				if (f.getName().equals(fieldName)) {
					return f;
				}
			}

			if (Element.class.isAssignableFrom(c.getSuperclass())) {
				c = (Class<? extends Element>) c.getSuperclass();
			} else {
				return null;
			}
		}

		return null;
	}

	public static <T extends Element> T lookupOrCreate(Hub h, ID bid, Function<ID, T> f) {
		T e = (T) h.indexes.byId.get(bid);
		return e != null ? e : f.apply(bid);
	}

	public T get() {
		return head;
	}

	public void set(T newE) {
		if (head != newE) {
			final var old = head;
			this.head = newE;
			tail.hub().eventList.add(new OutChangedEvent(tail, this, old, newE));
		}
	}

}
