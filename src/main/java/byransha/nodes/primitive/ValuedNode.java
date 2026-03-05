package byransha.nodes.primitive;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public abstract class ValuedNode<V> extends BNode {
	V value;
	public final List<ValueNodeListener<V>> valueChangeListeners = new ArrayList<>();

	public ValuedNode(BGraph g) {
		super(g);
	}

	public static interface ValueNodeListener<V> {
		void valueChangedTo(V v);
	}

	@Override
	public ObjectNode toJSONNode() {
		var r = super.toJSONNode();
		r.put("value", getAsString());
		return r;
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

	@Override
	public void reset() {
		set(defaultValue());
	}

	public void set(V newValue) {
		if (!canEdit(currentUser()))
			throw new RuntimeException(currentUser() + " is not allowed to set value");

		boolean valueChanging = newValue != value || (value != null && !value.equals(newValue));
		value = newValue;

		if (valueChanging) {
			valueChangeListeners.forEach(l -> l.valueChangedTo(newValue));
		}
	}

	public abstract V defaultValue();
}
