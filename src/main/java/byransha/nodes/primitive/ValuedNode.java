package byransha.nodes.primitive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeError;

public abstract class ValuedNode<V> extends BNode {
	V value;
	boolean valueRequired;
	public final List<ValueChangeListener<V>> valueChangeListeners = new ArrayList<>();

	public static interface ValueChangeListener<V> {
		void changed(ValuedNode<V> n, V formerValue, V newValue);
	}

	public ValuedNode(BGraph g) {
		super(g);
	}

	@Override
	public ObjectNode describeAsJSON() {
		var r = super.describeAsJSON();
		r.put("value", toString());
		return r;
	}

	@Override
	protected void fillErrors(List<NodeError> errs) {
		if (valueRequired && value == null) {
			errs.add(new NodeError(this, "a value is required"));
		}
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
		if (readOnly)
			throw new RuntimeException("can't change a read only valued node");

		if (!canEdit(currentUser()))
			throw new RuntimeException(currentUser() + " is not allowed to set value");

		V oldValue = value;
		boolean valueChange = newValue != value || (value != null && !value.equals(newValue));

		value = newValue;

		if (valueChange) {
			valueChangeListeners.forEach(l -> l.changed(this, oldValue, newValue));
		}

		if (g.eventList != null) {
			g.eventList.add(new ValuedNodeValueChangeEvent<V>(g, LocalDateTime.now(), this, oldValue, newValue));
		}
	}

	public abstract V defaultValue();

	@Override
	public String toString() {
		var v = get();
		return v == null ? super.toString() : v.toString();
	}

	protected abstract void writeValue(V v, ObjectOutput out) throws IOException;

	protected abstract V readValue(ObjectInput in) throws IOException;
}
