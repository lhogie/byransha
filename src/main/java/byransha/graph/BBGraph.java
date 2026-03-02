package byransha.graph;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import byransha.nodes.system.SystemNode;
import byransha.nodes.system.User;
import toools.Stop;

public class BBGraph extends BNode {
	private final ConcurrentMap<Integer, BNode> nodesById = new ConcurrentHashMap<>();

	private int nextID = 0;
	public final SystemNode systemNode;
	public final User systemUser = null;

	public BBGraph(File directory) throws Exception {
		super(null);
		setID(0);
		this.systemNode = new SystemNode(g, directory);
	}

	@Override
	public String whatIsThis() {
		return "a graph";
	}

	public synchronized int nextID() {
		while (nodesById.containsKey(nextID))
			++nextID;

		return nextID;
	}

	public long countNodes() {
		return nodesById.size();
	}

	public BNode root() {
		return this;
	}

	public BNode findByID(int id) {
		return nodesById.get(id);
	}

	public BNode forEachNode(Function<BNode, Stop> f) {
		for (BNode node : nodesById.values()) {
			if (f.apply(node) == Stop.yes) {
				return node;
			}
		}

		return null;
	}

	public <C extends BNode> C forEachNodeOfClass(Class<C> nodeClass, Function<C, Stop> f) {
		return (C) forEachNode(n -> nodeClass.isAssignableFrom(n.getClass()) ? f.apply((C) n) : Stop.no);
	}

	public <C extends BNode> C findFirst(Class<C> c, Predicate<C> p) {
		return forEachNodeOfClass(c, n -> Stop.stopIf(p.test(n)));
	}

	public <C extends BNode> C findFirstOr(Class<C> c, Predicate<C> p, Supplier<C> defaultValue) {
		var r = findFirst(c, p);
		return r == null && defaultValue != null ? defaultValue.get() : r;
	}

	public List<User> users() {
		return nodesById.values().stream().filter(n -> n instanceof User).map(n -> (User) n).toList();
	}

	@Override
	public String prettyName() {
		return "graph";
	}

	public BNode removeNode(int id) {
		return nodesById.remove(id);
	}

	void setID(BNode n, int newID) {
		BNode previous = nodesById.putIfAbsent(newID, this);

		if (previous != null && previous != this)
			throw new IllegalStateException(
					"can't add node " + this + " because its ID " + this.id() + " is already taken by: " + previous);
	}
}
