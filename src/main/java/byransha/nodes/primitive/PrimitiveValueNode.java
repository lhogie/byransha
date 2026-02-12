package byransha.nodes.primitive;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;

public abstract class PrimitiveValueNode<V> extends ValuedNode<V> {

	boolean undefined = false;

	public PrimitiveValueNode(BBGraph g, User user) {
		super(g, user);
	}

	@Override
	public ObjectNode toJSONNode(User user, int depth) {
		var r = super.toJSONNode(user, 0);
		r.put("value", getAsString());
		return r;
	}

	public abstract void fromString(String s, User user);
}
