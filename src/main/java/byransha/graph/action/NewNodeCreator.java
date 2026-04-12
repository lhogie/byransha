package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.action.FreezingAction.misc;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.graph.relection.ClassNode;
import byransha.nodes.lab.BusinessNode;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class NewNodeCreator extends FunctionAction<BNode, ListNode<BNode>> {
	ListNode<ClassNode> classes;

	public NewNodeCreator(BGraph g) {
		super(g, misc.class);
		classes = new ListNode<>(g, "Business class(es)");
	}

	public void addBusinessClassesIn(Package p) {
		try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(p.getName()).scan()) {
			for (var c : scanResult.getAllClasses().loadClasses()) {
				if (BusinessNode.class.isAssignableFrom(c)) {
//					System.out.println("adding " + c);
					addClass(c);
				}
			}
		}
	}

	@Override
	public boolean applies() {
		return true;
	}

	public void addClass(Class cla) {
		ClassNode cn = ClassNode.find(g, cla);
		classes.get().add(cn);
	}

	@Override
	public String toString() {
		return "node creator";
	}

	@Override
	public String whatItDoes() {
		return "creates a new node";
	}

	@Override
	public void impl() {
		result = new ListNode<BNode>(g, "newly created node(s)");
		result.get().addAll(classes.getSelected().stream().map(c -> c.newInstance()).toList());
	}
}
