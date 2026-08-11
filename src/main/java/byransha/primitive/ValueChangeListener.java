package byransha.primitive;

public interface ValueChangeListener<V> {
	void changed(ValuedElement<V> n, V formerValue, V newValue);
}