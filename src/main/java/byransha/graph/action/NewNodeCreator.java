package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.Category.list;
import byransha.graph.ProcedureAction;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.graph.relection.ClassNode;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class NewNodeCreator extends ProcedureAction<ListNode> {
	@ShowInKishanView
	ListNode<ClassNode> classes = new ListNode<>(g, "business class(es)", ClassNode.class);

	public NewNodeCreator(ListNode list) {
		super(list, list.class);
		addBusinessClassesIn(list.g.application.getClass().getPackage());
	}

	public void addBusinessClassesIn(Package p) {
		var contentClass = inputNode.contentClass;

		try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(p.getName()).scan()) {
			for (var c : scanResult.getAllClasses().loadClasses()) {
				try {
					if ((contentClass == null || contentClass.isAssignableFrom(c))
							&& c.getConstructor(BGraph.class) != null
							&& (c.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) == 0
							&& c.getDeclaringClass() == null) {
						System.out.println("adding " + c);
						addClass(c);
					}
				} catch (NoSuchMethodException e) {
					System.err.println("class " + c + " does not have a constructor with a BGraph parameter");
				}
			}
		}
	}

	@Override
	public boolean applies() {
		return true;
	}

	public void addClass(Class cla) {
		classes.get().add(g.indexes.byClass.getClassNodeFor(cla));
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
		inputNode.get().addAll(classes.getSelected().stream().map(c -> c.newInstance()).toList());
	}
}
