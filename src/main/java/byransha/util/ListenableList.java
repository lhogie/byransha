package byransha.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListenableList<T> extends ArrayList<T> {

	// ─── Listener Interface ──────────────────────────────────────────────────────

	public interface Listener<T> {
		void onAdded(int index, T element);

		void onRemoved(int index, T oldElement);

		void onSet(int index, T oldElement, T newElement);
	}

	// ─── Fields ──────────────────────────────────────────────────────────────────

	private final List<Listener<T>> listeners = new ArrayList<>();

	// ─── Constructors
	// ─────────────────────────────────────────────────────────────

	public ListenableList() {
		super();
	}

	public ListenableList(Collection<? extends T> c) {
		super(c);
	}

	// ─── Listener Management
	// ──────────────────────────────────────────────────────

	public void addListener(Listener<T> listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener<T> listener) {
		listeners.remove(listener);
	}

	// ─── Fire Helpers
	// ─────────────────────────────────────────────────────────────

	private void fireAdded(int index, T element) {
		listeners.forEach(l -> l.onAdded(index, element));
	}

	private void fireRemoved(int index, T oldElement) {
		listeners.forEach(l -> l.onRemoved(index, oldElement));
	}

	private void fireSet(int index, T oldElement, T newElement) {
		listeners.forEach(l -> l.onSet(index, oldElement, newElement));
	}

	// ─── Overrides
	// ────────────────────────────────────────────────────────────────

	@Override
	public boolean add(T element) {
		super.add(element);
		fireAdded(size() - 1, element);
		return true;
	}

	@Override
	public void add(int index, T element) {
		super.add(index, element);
		fireAdded(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		int startIndex = size();
		boolean changed = super.addAll(c);
		if (changed) {
			int i = startIndex;
			for (T element : c)
				fireAdded(i++, element);
		}
		return changed;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		boolean changed = super.addAll(index, c);
		if (changed) {
			int i = index;
			for (T element : c)
				fireAdded(i++, element);
		}
		return changed;
	}

	@Override
	public T remove(int index) {
		T removed = super.remove(index);
		fireRemoved(index, removed);
		return removed;
	}

	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index == -1)
			return false;
		super.remove(index);
		fireRemoved(index, (T) o);
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (int i = size() - 1; i >= 0; i--) {
			T element = get(i);
			if (c.contains(element)) {
				super.remove(i);
				fireRemoved(i, element);
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public T set(int index, T newElement) {
		T old = super.set(index, newElement);
		fireSet(index, old, newElement);
		return old;
	}

	@Override
	public void clear() {
		for (int i = size() - 1; i >= 0; i--) {
			T old = get(i);
			super.remove(i);
			fireRemoved(i, old);
		}
	}
}