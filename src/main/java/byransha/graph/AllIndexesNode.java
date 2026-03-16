package byransha.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import byransha.graph.action.list.ListNode;
import byransha.graph.relection.ClassNode;
import byransha.nodes.primitive.LongNode;

public class AllIndexesNode extends BNode {

	final LongNode nbClasses;
	final ListNode<ClassNode> classes;

	protected AllIndexesNode(BGraph g) {
		super(g);
		nbClasses = new LongNode(g) {
			@Override
			public Long get() {
				return (long) g.indexes.byClass.m.keys().size();
			}
		};
		nbClasses.readOnly = true;

		classes = new ListNode<>(g, "classes") {
			@Override
			public List<ClassNode> get() {
				return new ArrayList<>((Collection<ClassNode>) (Collection) g.indexes.byClass.m.get(ClassNode.class));
			}
		};

	}

	@Override
	public String whatIsThis() {
		return null;
	}

	@Override
	public String prettyName() {
		return null;
	}

}
