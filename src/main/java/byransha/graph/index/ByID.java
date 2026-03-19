package byransha.graph.index;

import java.util.Objects;

import byransha.graph.BNode;
import byransha.graph.Index;
import byransha.util.Base62;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class ByID extends Index {
	private final Int2ObjectMap<BNode> m = new Int2ObjectOpenHashMap<>();
	private final IntList freeSlots = new IntArrayList();

	public synchronized int forceIndex(BNode n, int newID) {
		Objects.requireNonNull(n);

		if (m.containsKey(newID)) {
			throw new IllegalStateException(newID + " is already used by node " + m.get(newID));
		}

		if (m.remove(n.id()) != n)
			throw new IllegalStateException();

		m.put(newID, n);
		return newID;
	}

	private synchronized void assignUniqueIndexTo(final BNode n) {
		if (n.id != -1)
			throw new IllegalStateException();

		int id = freeSlots.isEmpty() ? m.size() : freeSlots.removeLast();

		while (m.containsKey(id))
			++id;

		m.put(id, n);
		n.id = id;
	}

	@Override
	public void add(BNode n) {
		assignUniqueIndexTo(n);
	}

	@Override
	public void delete(BNode n) {
		int id = n.id();
		m.remove(id);
		freeSlots.add(id);
	}

	@Override
	public String strategy() {
		return "ID";
	}

	public BNode get(long id) {
		return m.get(id);
	}

	public BNode getByText(String id) {
		return m.get(Base62.decode(id));
	}
}