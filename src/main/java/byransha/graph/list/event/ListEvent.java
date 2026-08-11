package byransha.graph.list.event;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import byransha.ID;
import byransha.event.Event;
import byransha.graph.Element;
import byransha.graph.list.action.ListNode;

public abstract class ListEvent<T extends Element> extends Event {
	protected ListNode list;
	protected int index;
	protected T element;

	public ListEvent(ListNode list, T element, int index) {
		super(list);
		this.list = list;
		this.index = index;
		this.element = element;

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(list.id());
		out.writeObject(element.id());
		out.writeInt(index);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		list = (ListNode) hub().indexes.byId.get((ID) in.readObject());
		element = (T) hub().indexes.byId.get((ID) in.readObject());
		index = in.readInt();
	}
}
