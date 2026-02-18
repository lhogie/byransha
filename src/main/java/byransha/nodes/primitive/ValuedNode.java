package byransha.nodes.primitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public abstract class ValuedNode<V> extends BNode {
	V value;
	public final List<ValueNodeListener<V>> listeners = new ArrayList<>();

	public ValuedNode(BBGraph g) {
		super(g);
	}

	public static interface ValueNodeListener<V> {
		void valueChangedTo(V v);
	}

	protected abstract byte[] valueToBytes(V v) throws IOException;

	protected abstract V bytesToValue(byte[] bytes) throws IOException;

	@Override
	public final String toString() {
		return getClass().getSimpleName() + ": " + (value == null ? "no value" : value.toString());
	}

	public String getAsString() {
		return value != null ? value.toString() : "";
	}

	public V get() {
		if (!canSee(currentUser()))
			throw new RuntimeException(currentUser() + " is not allowed to read the value");

		return value;
	}

	public V getOrDefault(V defaultValue) {
		var value = get();

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	public void set(V newValue) {
		if (!canEdit(currentUser()))
			throw new RuntimeException(currentUser() + " is not allowed to set value");

		boolean valueChanging = newValue != value || (value != null && !value.equals(newValue));
		value = newValue;

		if (valueChanging) {
			listeners.forEach(l -> l.valueChangedTo(newValue));
		}
	}

	public abstract V defaultValue();
}
