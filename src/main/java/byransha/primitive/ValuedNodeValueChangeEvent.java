package byransha.primitive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.util.UUID;

import byransha.event.Event;
import byransha.graph.Hub;

public class ValuedNodeValueChangeEvent<V> extends Event {
	ValuedNode<V> node;
	V oldValue;
	V newValue;

	public ValuedNodeValueChangeEvent(Hub g, LocalDateTime date, ValuedNode<V> node, V oldValue, V newValue) {
		super(g, date);
		this.node = node;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public void apply(Hub g) throws Throwable {
		node.set(newValue);
	}

	@Override
	public void undo(Hub g) throws Throwable {
		node.set(oldValue);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeLong(node.id().getMostSignificantBits());
		out.writeLong(node.id().getLeastSignificantBits());
		node.writeValue(oldValue, out);
		node.writeValue(newValue, out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		var uuid = new UUID(in.readLong(), in.readLong());
		node = (ValuedNode<V>) g.indexes.byId.get(uuid);
		oldValue = node.readValue(in);
		newValue = node.readValue(in);
	}

}