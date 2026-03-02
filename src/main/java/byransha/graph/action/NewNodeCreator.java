package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.relection.ClassNode;
import byransha.nodes.lab.BusinessNode;
import byransha.nodes.lab.I3S;
import byransha.nodes.primitive.ListNode;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class NewNodeCreator extends NodeAction<BNode, ListNode<BNode>> {
	ListNode<ClassNode> classes;

	public NewNodeCreator(BBGraph g) {
		super(g, g);
		classes = new ListNode<>(g);
	}

	public void addClasses(Package p) {
		try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(p.getName()).scan()) {
			for (var c : scanResult.getAllClasses().loadClasses()) {
				if (BusinessNode.class.isAssignableFrom(c)) {
					System.out.println("adding " + c);
					addClass(c);
				}
			}
		}
	}

	public void addClass(Class cla) {
		ClassNode cn = g.findFirstOr(ClassNode.class, n -> n.clazz == cla, () -> new ClassNode(g, cla));
		classes.get().add(cn);
	}

	@Override
	public String prettyName() {
		return "node creator";
	}

	@Override
	public String whatItDoes() {
		return "creates a new node";
	}

	@Override
	public ActionResult<BNode, ListNode<BNode>> exec() {
		var instanceList = new ListNode<BNode>(g);
		instanceList.get().addAll(classes.getSelected().stream().map(c -> c.newInstance()).toList());
		return createResultNode(instanceList);
	}
}
