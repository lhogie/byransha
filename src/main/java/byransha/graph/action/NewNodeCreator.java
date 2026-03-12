package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.relection.ClassNode;
import byransha.nodes.lab.BusinessNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.system.ChatNode;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class NewNodeCreator extends NodeAction<BNode, ListNode<BNode>> {
	ListNode<ClassNode> classes;

	public NewNodeCreator(BGraph g) {
		super(g, g);
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
	public boolean applies(ChatNode chat) {
		return true;
	}

	public void addClass(Class cla) {
		ClassNode cn = ClassNode.find(g, cla);
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
	public ActionResult<BNode, ListNode<BNode>> exec(ChatNode chat) {
		var instanceList = new ListNode<BNode>(g, "newly created node(s)");
		instanceList.get().addAll(classes.getSelected().stream().map(c -> c.newInstance()).toList());
		return createResultNode(instanceList, false);
	}
}
