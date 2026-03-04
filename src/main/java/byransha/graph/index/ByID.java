package byransha.graph.index;

import byransha.graph.BNode;
import byransha.graph.Index;
import byransha.graph.BGraph;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class ByID extends Index {
	 final Int2ObjectMap<BNode> m = new Int2ObjectOpenHashMap<>();

	protected ByID(BGraph g) {
		super(g);
	}

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
	public void arcDeleted(BNode from, BNode to) {
	}

	@Override
	public void arcAdded(BNode from, BNode to) {
	}

	@Override
	public void idChanged(int oldID, int newID) {
		BNode n = m.remove(oldID);
		m.put(newID, n);
	}

	@Override
	public String strategy() {
		return "ID";
	}

	public BNode get(int id) {
		return m.get(id);
	}
}