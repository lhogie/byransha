package byransha.graph.action;

import java.lang.reflect.Constructor;

import byransha.graph.BNode;
import byransha.graph.Category.list;
import byransha.graph.ProcedureAction;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.graph.relection.ClassNode;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class NewNodeCreator extends ProcedureAction<ListNode> {
	
	
	
	@ShowInKishanView
	ListNode<ClassNode> classes = new ListNode<>(this, "business class(es)", ClassNode.class);

	public NewNodeCreator(ListNode list) {
		super(list, list.class);
		addBusinessClassesIn(g().application.getClass().getPackage());
	}

	public void addBusinessClassesIn(Package p) {
		var contentClass = inputNode.contentClass;

		try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(p.getName()).scan()) {
			for (var c : scanResult.getAllClasses().loadClasses()) {
				if ((contentClass == null || contentClass.isAssignableFrom(c)
						&& (c.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) == 0
						&& c.getDeclaringClass() == null)) {
					var constr = getC(c);

					if (constr != null) {
						addClass(c);
					} else {
						System.err.println("class " + c + " does not have a constructor with a BGraph parameter");
					}
				}
			}
		}
	}

	private Constructor getC(Class c) {
		for (var constr : c.getConstructors()) {
			if (constr.getParameterCount() == 1 && BNode.class.isAssignableFrom(constr.getParameterTypes()[0])) {
				return constr;
			}
		}
		return null;
	}

	@Override
	public boolean applies() {
		return true;
	}

	public void addClass(Class cla) {
		classes.get().add(g().indexes.byClass.getClassNodeFor(cla));
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
		inputNode.get().addAll(classes.getSelected().stream().map(c -> c.newInstance(this)).toList());
	}
}
