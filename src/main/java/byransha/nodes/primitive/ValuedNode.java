package byransha.nodes.primitive;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public abstract class ValuedNode<V> extends BNode {
	V value;
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
		r.put("value", getValueAsString());
		return r;
	}

	public String getValueAsString() {
		return value != null ? value.toString() : "";
	}

	public abstract V valueFromString(String s);

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
//			g.eventList.add(new ValuedNodeValueChangeEvent<V>(g, LocalDateTime.now(), id(), oldValue, newValue));
		}
	}

	public abstract V defaultValue();
}
