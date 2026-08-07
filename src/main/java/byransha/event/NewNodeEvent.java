package byransha.event;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.util.UUID;

import byransha.graph.BNode;
import byransha.graph.Hub;

public class NewNodeEvent<N extends BNode> extends Event {
	Class<N> clazz;
	UUID nodeId;

	public NewNodeEvent(BNode n) {
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
		var n = clazz.getConstructor(Hub.class).newInstance(g);

		if (nodeId != null) {
			g.indexes.byId.forceIndex(n, nodeId);
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(clazz);
		out.writeLong(nodeId.getMostSignificantBits());
		out.writeLong(nodeId.getLeastSignificantBits());
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		clazz = (Class) in.readObject();
		nodeId = new UUID(in.readLong(), in.readLong());
	}

}