package byransha.primitive;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Files;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameter;
import byransha.ProblemInElement;
import byransha.service.system.Byransha;

public abstract class ValuedElement<V> extends Element {
	V value;
	boolean valueRequired;
	public final List<ValueChangeListener<V>> valueChangeListeners = new ArrayList<>();
	private boolean mirrorToDisk = false;

	public ValuedElement(InstantiationParameter p) {
		super(p);
//		shownToDisk = enclosingBusinessNode() == null; // all technical info is printed on disk
	}

	public ValuedElement(Element parent, ID id) {
		super(parent, id);
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
	protected void fillErrors(List<ProblemInElement> errs) {
		if (valueRequired && value == null) {
			errs.add(new ProblemInElement(this, "a value is required"));
		}
	}

	public V get() {
		if (false)// !canSee(g().currentUser()))
			throw new RuntimeException(hub().currentUser() + " is not allowed to read the value");

		return this.value;
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
		changeValue(newValue);

		if (generateEvents()) {
			hub().eventList.add(new ValueChangeEvent<V>(LocalDateTime.now(), this, oldValue, newValue));
		}
	}

	public void setByEvent(V newValue, ValueChangeEvent e) {
		changeValue(newValue);
	}

	private void changeValue(V newValue) {
		V oldValue = value;
		boolean valueChange = newValue != value || (value != null && !value.equals(newValue));
		this.value = newValue;

		if (valueChange) {
			synchronized (valueChangeListeners) {
				valueChangeListeners.forEach(l -> l.changed(this, oldValue, newValue));
			}
		}

		if (mirrorToDisk) {
			writeValueToDisk();
		}
	}

	public void writeValueToDisk() {
		try {
			var s = toString();
			var f = new File(hub().byransha.homeDirectory, "valued_nodes/" + rolePath() + ".txt");

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
