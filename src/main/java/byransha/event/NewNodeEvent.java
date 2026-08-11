package byransha.event;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;

import byransha.Element;
import byransha.ID;
import byransha.service.system.Hub;

public class NewNodeEvent<N extends Element> extends Event {
	Class<N> clazz;
	ID nodeId;

	public NewNodeEvent(Element n) {
		super(n.hub(), LocalDateTime.now());
		this.clazz = (Class<N>) n.getClass();
		this.nodeId = n.id();
	}

	public NewNodeEvent(Hub g, LocalDateTime date) {
		super(g, date);
	}

	@Override
	public void undo(Hub g) throws Throwable {
		g.indexes.byId.get(nodeId).delete();
	}

	@Override
	public void apply(Hub g) throws Throwable {
		var n = clazz.getConstructor(Hub.class, ID.class).newInstance(g, nodeId);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(clazz);
		out.writeObject(nodeId);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		clazz = (Class) in.readObject();
		nodeId = (ID) in.readObject();
	}

}