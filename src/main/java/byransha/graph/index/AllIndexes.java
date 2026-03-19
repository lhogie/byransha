package byransha.graph.index;

import java.util.Objects;

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
		Objects.requireNonNull(n);
		byId.add(n);
		nodesList.add(n);
		byClass.add(n);
		reverseNavigation.add(n);
	}

	@Override
	public void delete(BNode n) {
		nodesList.delete(n);
		byId.delete(n);
		byClass.delete(n);
		reverseNavigation.delete(n);
	}

	@Override
	public String strategy() {
		return "mixed";
	}

}