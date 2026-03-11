package byransha.graph.index;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import butils.Stop;
import byransha.graph.BNode;
import byransha.graph.Index;

public class ByClass extends Index {

	public MultiValuedMap<Class, BNode> m = new HashSetValuedHashMap<>();

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
		n.ascendSuperClassesUntil(BNode.class, clazz -> m.put(clazz, n));
	}

	@Override
	public void delete(BNode n) {
		n.ascendSuperClassesUntil(BNode.class, clazz -> m.removeMapping(clazz, n));
	}

	@Override
	public String strategy() {
		return "class";
	}

}