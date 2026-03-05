package byransha.graph.index;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Index;

public class AllIndexes extends Index {
	public final ByID byId;
	public final ByClass byClass;
	public final NodeList nodesList;
	public final ReverseNavigation reverseNavigation;

	public AllIndexes(BGraph g) {
		super(g);
		g.listeners.add(byId = new ByID(g));
		g.listeners.add(byClass = new ByClass(g));
		g.listeners.add(nodesList = new NodeList(g));
		g.listeners.add(reverseNavigation = new ReverseNavigation(g));
	}

	public long numberOfNodes() {
		return nodesList.size();
	}

	@Override
	public void add(BNode n) {
		byId.index(n);
		nodesList.l.add(n);
		byClass.m.put(n.getClass(), n);
	}

	@Override
	public void delete(BNode n) {
		nodesList.l.remove(n);
		byId.m.remove(n.id());
		byClass.m.removeMapping(n.getClass(), n);

		for (var inl : reverseNavigation.m.remove(n)) {

		}
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
		return "mixed";
	}

}