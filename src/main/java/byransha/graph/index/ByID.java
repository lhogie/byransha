package byransha.graph.index;

import java.util.Random;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.Index;
import byransha.util.Base62;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ByID extends Index {
	protected ByID(AllIndexes allIndexes) {
		super(null);
		// super(allIndexes);
	}

	private final Object2ObjectOpenHashMap<ID, Element> m = new Object2ObjectOpenHashMap<>();
	final Random r = new Random();

	/*
	public synchronized UUID forceIndex(BNode n, UUID newID) {
		Objects.requireNonNull(n);

		if (m.containsKey(newID)) {
			throw new IllegalStateException(newID + " is already used by node " + m.get(newID));
		}

		if (m.remove(n.id()) != n)
			throw new IllegalStateException();

		n.setID(newID);
		m.put(newID, n);
		return newID;
	}*/

	@Override
	public void add(Element n) {
		m.put(n.id(), n);
	}

	@Override
	public void delete(Element n) {
		m.remove(n.id());
	}

	@Override
	public String strategy() {
		return "ID";
	}

	public Element get(ID id) {
		return m.get(id);
	}

	public Element getByText(String id) {
		return m.get(Base62.decode(id));
	}
}