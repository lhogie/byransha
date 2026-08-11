package byransha.graph;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import byransha.ID;
import byransha.Out;
import byransha.event.Event;

public class OutChangedEvent extends Event {
	private ID elementID;
	private String fieldName;
	private ID oldValue;
	private ID newValue;

	public OutChangedEvent(Element element, Out out, Element a, Element b) {
		super(element);
		this.elementID = element.id();
		this.fieldName = out.field.getName();
		this.oldValue = a.id();
		this.newValue = b.id();
	}

	@Override
	public void apply(Hub g) throws Throwable {
		throw new IllegalStateException();

	}

	@Override
	public void undo(Hub g) throws Throwable {
		throw new IllegalStateException();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(elementID);
		out.writeUTF(fieldName);
		out.writeObject(oldValue);
		out.writeObject(newValue);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		elementID = (ID) in.readObject();
		fieldName = in.readUTF();
		oldValue = (ID) in.readObject();
		newValue = (ID) in.readObject();
	}
}
