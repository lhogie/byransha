package byransha.graph.action.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import butils.Stop;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.relection.ClassNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.PrimitiveValueNode;

public class SearchByClass extends Search {
	public ListNode<ClassNode> availableClasses;

	public SearchByClass(BGraph g, BNode src) {
		super(g, src);
		availableClasses = new ListNode(g, "searcheable classes");

		// update the list of classes when the depth changes
		depth.changeListeners.add(e -> {
			var classes = new HashSet<Class>();
			bfs(depth.get(), n -> true, (node, d) -> classes.add(node.getClass()));
			var classList = new ArrayList<Class>(classes);
			Collections.sort(classList, (a, b) -> a.getSimpleName().compareTo(b.getSimpleName()));
			List<ClassNode> l = classes.stream().map(c -> g.i.byClass.forEachNodeOfClass(ClassNode.class, n -> Stop.no))
					.toList();
			availableClasses.set(l);
		});
	}

	@Override
	protected boolean accept(BNode n) {
		return !(n instanceof PrimitiveValueNode);
	}

	@Override
	public String whatItDoes() {
		return "search all nodes containing a given string";
	}
}