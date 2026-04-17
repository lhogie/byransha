package byransha.nodes.primitive;

public interface ValueChangeListener<V> {
	void changed(ValuedNode<V> n, V formerValue, V newValue);
}