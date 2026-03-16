package byransha.graph.index;

import byransha.graph.BNode;
import byransha.graph.Index;

public class AllIndexes extends Index {
	public final ByID byId;
	public final ByClass byClass;
	public final NodeList nodesList;
	public final ReverseNavigation reverseNavigation;

	public AllIndexes() {
		byId = new ByID();
		byClass = new ByClass();
		nodesList = new NodeList();
		reverseNavigation = new ReverseNavigation();
	}

	public long numberOfNodes() {
		return nodesList.size();
	}

	@Override
	public void add(BNode n) {
		byId.index(n);
		nodesList.l.add(n);
		byClass.add(n);
	}

	@Override
	public void delete(BNode n) {
		nodesList.l.remove(n);
		byId.m.remove(n.id());
		n.ascendSuperClassesUntil(n.getClass(), BNode.class, clazz -> byClass.m.removeMapping(clazz, n));

		for (var inl : reverseNavigation.m.remove(n)) {

		}
	}

	@Override
	public String strategy() {
		return "mixed";
	}

}