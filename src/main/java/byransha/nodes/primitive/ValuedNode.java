package byransha.nodes.primitive;

import java.io.IOException;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.User;

public abstract class ValuedNode<V> extends BNode {
	V value;

	public ValuedNode(BBGraph g, User user) {
		super(g, user);
	}

	protected abstract byte[] valueToBytes(V v) throws IOException;

	protected abstract V bytesToValue(byte[] bytes, User user) throws IOException;

	@Override
	public final String toString() {
		return getClass().getSimpleName() + ": " + value == null ? "no value" : value.toString();
	}

	public String getAsString() {
		return value != null ? value.toString() : "";
	}

	public V get() {
		var user = g.systemUser;
		if (!canSee(user))
			throw new RuntimeException(user + " is not allowed to read the value");

		return value;
	}

	public void set(V v, User user) {
		if (!canEdit(user))
			throw new RuntimeException(user + " is not allowed to set value");

		value = v;
	}

	public abstract V defaultValue();
}
