package byransha.graph.index;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import butils.Stop;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Index;

public class ByClass extends Index {

	MultiValuedMap<Class<? extends BNode>, BNode> m = new HashSetValuedHashMap<>();

	protected ByClass(BGraph g) {
		super(g);
	}

	public <C extends BNode> C forEachNodeOfClass(Class<C> nodeClass, Function<C, Stop> f) {
		for (var c : m.keySet()) {
			if (nodeClass.isAssignableFrom(c)) {
				for (var n : m.get(c)) {
					if (f.apply((C) n) == Stop.yes) {
						return (C) n;
					}
				}
			}
		}

		return null;
	}

	public <C extends BNode> C findFirst(Class<C> c, Predicate<C> p) {
		return forEachNodeOfClass(c, n -> Stop.stopIf(p.test(n)));
	}

	public <C extends BNode> C findFirstOr(Class<C> c, Predicate<C> p, Supplier<C> defaultValue) {
		var r = findFirst(c, p);
		return r == null && defaultValue != null ? defaultValue.get() : r;
	}

	@Override
	public void add(BNode n) {
		m.put(n.getClass(), n);
	}

	@Override
	public void delete(BNode n) {
		m.removeMapping(n.getClass(), n);
	}

	@Override
	public void arcDeleted(BNode from, BNode to) {
	}

	@Override
	public void arcAdded(BNode from, BNode to) {
	}

	@Override
	public void idChanged(int oldID, int newID) {
	}

	@Override
	public String strategy() {
		return "class";
	}

	public <N extends BNode> List<N> get(Class<N> c) {
		return get(c).stream().map(n -> (N) n).toList();
	}

}