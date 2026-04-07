package byransha.util;

public interface ListChangeListener<E> {
	void onAdd(E element, int index);

	void onRemove(E element, int index);
}