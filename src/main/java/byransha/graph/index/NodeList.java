package byransha.graph.index;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import butils.Stop;
import byransha.graph.BNode;
import byransha.graph.Index;

public class NodeList extends Index {
	 final List<BNode> l = new ArrayList<>();

	

	public BNode forEachNode(Function<BNode, Stop> f) {
		for (BNode node : l) {
			if (f.apply(node) == Stop.yes) {
				return node;
			}
		}

		return null;
	}

	
	@Override
	public void add(BNode n) {
		l.add(n);
	}

	@Override
	public void delete(BNode n) {
		l.remove(n);
	}


	@Override
	public String strategy() {
		return "list of nodes";
	}

	public long size() {
		return l.size();
	}
}