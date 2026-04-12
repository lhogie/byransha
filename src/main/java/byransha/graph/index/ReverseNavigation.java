package byransha.graph.index;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import byransha.graph.BNode;
import byransha.graph.Index;
import byransha.graph.index.ReverseNavigation.InLink;

public class ReverseNavigation extends Index {
	MultiValuedMap<BNode, InLink> m = new HashSetValuedHashMap<>();

	public record InLink(String role, BNode source) {
		@Override
		public String toString() {
			return source + "." + role;
		}
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
	}

	@Override
	public void delete(BNode n) {
		m.remove(n).forEach(inl -> inl.source.removeOut(n));
	}

	@Override
	public String strategy() {
		return "reverse navigation";
	}

	public void forEachInOf(BNode n, Consumer<InLink> consumer) {
		m.get(n).forEach(consumer);
	}
}