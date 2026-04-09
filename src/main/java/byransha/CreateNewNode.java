package byransha;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;

import byransha.event.Event;
import byransha.graph.BGraph;
import byransha.graph.BNode;

public class CreateNewNode<N extends BNode> extends Event {

	Class<N> clazz;
	int nodeId = -1;

	public CreateNewNode(BGraph g, LocalDateTime date) {
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
		out.writeInt(nodeId);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		clazz = (Class) in.readObject();
		nodeId = in.readInt();
	}

}