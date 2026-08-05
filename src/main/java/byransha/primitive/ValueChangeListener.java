package byransha.primitive;

public interface ValueChangeListener<V> {
	void changed(ValuedNode<V> n, V formerValue, V newValue);
}