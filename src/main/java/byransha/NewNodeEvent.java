package byransha;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;

import org.checkerframework.checker.units.qual.N;

import byransha.event.Event;
import byransha.graph.BGraph;
import byransha.graph.BNode;

public class NewNodeEvent<N extends BNode> extends Event {
	Class<N> clazz;
	long nodeId = -1;

	public NewNodeEvent(BNode n) {
		super(n.g(), LocalDateTime.now());
		this.clazz = (Class<N>) n.getClass();
		this.nodeId = n.id;
	}

	public NewNodeEvent(BGraph g, LocalDateTime date) {
		super(g, date);
	}

	@Override
	public void undo(BGraph g) throws Throwable {
		g.indexes.byId.get(nodeId).delete();
	}

	@Override
	public void apply(BGraph g) throws Throwable {
		var n = clazz.getConstructor(BGraph.class).newInstance(g);

		if (nodeId != -1) {
			g.indexes.byId.forceIndex(n, nodeId);
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(clazz);
		out.writeLong(nodeId);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		clazz = (Class) in.readObject();
		nodeId = in.readLong();
	}

}