package byransha.action.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import byransha.Element;
import byransha.graph.relection.ClassNode;
import byransha.list.action.ListNode;
import byransha.primitive.PrimitiveValueNode;
import byransha.util.Stop;

public class SearchByClass extends Search {
	public ListNode<ClassNode> availableClasses = new ListNode<>(this, null, "searcheable classes", ClassNode.class);

	public SearchByClass(Element src) {
		super(src);

		// update the list of classes when the depth changes
		depth.addValueChangeListener((depthNode, oldValue, newValue) -> {
			var classes = new HashSet<Class>();
			bfs(depth.get(), n -> true, (node, d) -> classes.add(node.getClass()));
			var classList = new ArrayList<Class>(classes);
			Collections.sort(classList, (a, b) -> a.getSimpleName().compareTo(b.getSimpleName()));
			List<ClassNode> l = classes.stream()
					.map(c -> hub().indexes.byClass.forEachNodeAssignableTo(ClassNode.class, n -> Stop.no)).toList();
			availableClasses.set(l);
		});
	}

	@Override
	protected boolean accept(Element n) {
		return !(n instanceof PrimitiveValueNode);
	}

	@Override
	public String whatItDoes() {
		return "search all nodes containing a given string";
	}
}