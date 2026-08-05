package byransha.primitive;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Files;

import byransha.graph.BNode;
import byransha.graph.NodeError;
import byransha.system.Byransha;

public abstract class ValuedNode<V> extends BNode {
	V value;
	boolean valueRequired;
	public final List<ValueChangeListener<V>> valueChangeListeners = new ArrayList<>();
	private boolean shownOnDisk;

	public ValuedNode(BNode parent) {
		super(parent);
		shownOnDisk = enclosingBusinessNode() == null; // all technical info is printed on disk
	}

	public void addValueChangeListener(ValueChangeListener<V> l) {
		synchronized (valueChangeListeners) {
			valueChangeListeners.add(l);
		}
	}

	public void removeValueChangeListener(ValueChangeListener<V> l) {
		synchronized (valueChangeListeners) {
			valueChangeListeners.remove(l);
		}
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
		if (false)// !canSee(g().currentUser()))
			throw new RuntimeException(hub().currentUser() + " is not allowed to read the value");

		return value;
	}

	public V getOrDefault(V defaultValue) {
		var value = get();

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	public void reset() {
		set(defaultValue());
	}

	public void set_checkPermissions(V newValue) {
		if (hub() != null && !canEdit(hub().currentUser()))
			throw new RuntimeException(hub().currentUser() + " is not allowed to set value");

		set(newValue);
	}

	public void set(V newValue) {
		if (userEditable)
			throw new RuntimeException("can't change a read only valued node");

		V oldValue = value;
		boolean valueChange = newValue != value || (value != null && !value.equals(newValue));

		value = newValue;

		if (valueChange) {
			synchronized (valueChangeListeners) {
				valueChangeListeners.forEach(l -> l.changed(this, oldValue, newValue));
			}
		}

		if (generateEvents) {
			var g = hub();
			if (g.eventList != null) {
//				g.eventList.add(new ValuedNodeValueChangeEvent<V>(g, LocalDateTime.now(), this, oldValue, newValue));
			}
		}

		if (shownOnDisk) {
//			writeValueToDisk();
		}
	}

	public void writeValueToDisk() {
		try {
			var s = toString();
			var f = new File(Byransha.homeDirectory, "valued_nodes/" + rolePath() + ".txt");

			if (s != null) {
				f.getParentFile().mkdirs();
				Files.write(s.getBytes(), f);
			} else if (f.exists()) {
				f.delete();
			}
		} catch (IOException ioError) {
			hub().errorLog.add(ioError);
		}
	}

	public abstract V defaultValue();

	@Override
	public String toString() {
		var v = get();
		return v == null ? "null" : v.toString();
	}

	protected abstract void writeValue(V v, ObjectOutput out) throws IOException;

	protected abstract V readValue(ObjectInput in) throws IOException;
}
