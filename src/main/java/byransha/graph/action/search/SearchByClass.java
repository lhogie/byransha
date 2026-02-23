package byransha.graph.action.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.relection.ClassNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.PrimitiveValueNode;
import toools.Stop;

public class SearchByClass extends Search {
	public ListNode<ClassNode> availableClasses;

	public SearchByClass(BBGraph g) {
		super(g);
		availableClasses = new ListNode(g);

		// update the list of classes when the depth changes
		depth.listeners.add(e -> {
			var classes = new HashSet<Class>();
			bfs(depth.get(), n -> true, (node, d) -> classes.add(node.getClass()));
			var classList = new ArrayList<Class>(classes);
			Collections.sort(classList, (a, b) -> a.getSimpleName().compareTo(b.getSimpleName()));
			List<ClassNode> l = classes.stream().map(c -> g.forEachNodeOfClass(ClassNode.class, n -> Stop.no)).toList();
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