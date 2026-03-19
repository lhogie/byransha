package byransha.util;

public interface ListChangeListener<E> {
	void onAdd(E element);

	void onRemove(E element);
}