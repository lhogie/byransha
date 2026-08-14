package byransha.primitive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;

import byransha.ID;
import byransha.event.Event;
import byransha.service.system.Hub;

public class ValueChangeEvent<V> extends Event {
	ValuedElement<V> element;
	V oldValue;
	V newValue;

	public ValueChangeEvent(LocalDateTime date, ValuedElement<V> node, V oldValue,
			V newValue) {
		super(node, date);
		this.element = node;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public void apply(Hub g) throws Throwable {
		element.setByEvent(newValue, this);
	}

	@Override
	public void undo(Hub g) throws Throwable {
		element.set(oldValue);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(element.id());
		element.writeValue(oldValue, out);
		element.writeValue(newValue, out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		var uuid = (ID) in.readObject();
		element = (ValuedElement<V>) hub().indexes.byId.get(uuid);
		oldValue = element.readValue(in);
		newValue = element.readValue(in);
	}

}