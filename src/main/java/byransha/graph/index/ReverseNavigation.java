package byransha.graph.index;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Index;

public class ReverseNavigation extends Index {
	MultiValuedMap<BNode, InLink> m = new HashSetValuedHashMap<>();

	public record InLink(String role, BNode source) {
		@Override
		public String toString() {
			return source + "." + role;
		}
	}

	protected ReverseNavigation(BGraph g) {
		super(g);
	}

	private List<InLink> computeIns(List<BNode> nodes) {
		List<InLink> refs = new ArrayList<>();

		nodes.forEach(node -> {
			node.forEachOut((target, role) -> {
				if (target != null && target == node) {
					refs.add(new InLink(role, node));
				}
			});
		});

		return refs;
	}

	@Override
	public void add(BNode n) {
		m.put(n, null);
	}

	@Override
	public void delete(BNode n) {
		m.remove(n);
	}

	@Override
	public void arcDeleted(BNode from, BNode to) {
		m.removeMapping(from, to);
	}

	@Override
	public void arcAdded(BNode from, BNode to) {
	}

	@Override
	public void idChanged(int oldID, int newID) {
	}

	@Override
	public String strategy() {
		return "reverse navigation";
	}

	public void forEachInOf(BNode n, Consumer<InLink> consumer) {
		m.get(n).forEach(consumer);
	}
}