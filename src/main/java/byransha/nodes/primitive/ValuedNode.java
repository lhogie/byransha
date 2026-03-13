package byransha.nodes.primitive;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public abstract class ValuedNode<V> extends BNode {
	V value;
	public boolean readOnly;


	public ValuedNode(BGraph g) {
		super(g);
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
			changeListeners.forEach(l -> l.changed(this));
		}
	}

	public abstract V defaultValue();
}
