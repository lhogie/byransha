package byransha.nodes.primitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.User;

public abstract class ValuedNode<V> extends BNode {
	V value;
	public final List<ValueNodeListener<V>> listeners = new ArrayList<>();

	public ValuedNode(BBGraph g, User user) {
		super(g, user);
	}

	public static interface ValueNodeListener<V> {
		void valueChangedTo(V v);
	}

	protected abstract byte[] valueToBytes(V v) throws IOException;

	protected abstract V bytesToValue(byte[] bytes, User user) throws IOException;

	@Override
	public final String toString() {
		return getClass().getSimpleName() + ": " +( value == null ? "no value" : value.toString());
	}

	public String getAsString() {
		return value != null ? value.toString() : "";
	}

	public V get() {
		var user = g.systemUser;
		if (!canSee(user))
			throw new RuntimeException(user + " is not allowed to read the value");

		return value;
	}

	public V getOrDefault(V defaultValue) {
		var value = get();

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	public void set(V newValue, User user) {
		if (!canEdit(user))
			throw new RuntimeException(user + " is not allowed to set value");

		boolean valueChanging = newValue != value || (value != null && !value.equals(newValue));
		value = newValue;

		if (valueChanging) {
			listeners.forEach(l -> l.valueChangedTo(newValue));
		}
	}

	public abstract V defaultValue();
}
