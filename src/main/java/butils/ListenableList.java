package butils;

import java.util.ArrayList;
import java.util.List;

public class ListenableList<E> extends ArrayList<E> {

	public static interface ChangeListener<E> {
		void onAdd(E element);

		void onRemove(E element);
	}

	private final List<ChangeListener<E>> listeners = new ArrayList<>();

	public void addChangeListener(ChangeListener<E> l) {
		listeners.add(l);
	}

	public void removeChangeListener(ChangeListener<E> l) {
		listeners.remove(l);
	}

	@Override
	public boolean add(E e) {
		boolean result = super.add(e);
		if (result)
			listeners.forEach(l -> l.onAdd(e));
		return result;
	}

	@Override
	public E remove(int index) {
		E removed = super.remove(index);
		listeners.forEach(l -> l.onRemove(removed));
		return removed;
	}

	@Override
	public boolean remove(Object o) {
		boolean result = super.remove(o);
		if (result)
			listeners.forEach(l -> l.onRemove((E) o));
		return result;
	}
}