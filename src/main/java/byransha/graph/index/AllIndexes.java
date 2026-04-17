package byransha.graph.index;

import java.util.Objects;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Index;

public class AllIndexes extends Index {
	public final ByID byId;
	public final ByClass byClass;
	public final NodeList nodesList;

	public AllIndexes(BGraph g) {
		super(g);
		byId = new ByID(this);
		byClass = new ByClass(g);
		nodesList = new NodeList(this);
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
	}

	@Override
	public void delete(BNode n) {
		nodesList.delete(n);
		byId.delete(n);
		byClass.delete(n);
	}

	@Override
	public String strategy() {
		return "mixed";
	}

}