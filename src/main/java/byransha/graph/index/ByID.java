package byransha.graph.index;

import butils.Base62;
import byransha.graph.BNode;
import byransha.graph.Index;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class ByID extends Index {
	final Long2ObjectMap<BNode> m = new Long2ObjectOpenHashMap<>();

	private final IntList freeSlots = new IntArrayList();

	public synchronized void removeFromIndex(BNode n) {
		int id = n.id();
		m.remove(id);
		freeSlots.add(id);
	}

	public synchronized int reindex(int oldID, int newID) {
		if (m.containsKey(newID))
			throw new IllegalStateException(newID + " is already used by node " + m.get(newID));

		var n = m.remove(oldID);
		m.put(newID, n);
		return newID;
	}

	public synchronized int index(BNode n) {
		int id = freeSlots.isEmpty() ? m.size() : freeSlots.removeLast();

		while (m.containsKey(id))
			++id;

		m.put(id, n);
		n.id = id;
		return id;
	}

	@Override
	public void add(BNode n) {
		m.put(n.id(), n);
	}

	@Override
	public void delete(BNode n) {
		m.remove(n.id());
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