package byransha.graph.relection;

import byransha.graph.Category;
import byransha.graph.ProcedureAction;

public class LinkAction extends ProcedureAction<ClassNode> {

	public static class type extends Category {
	}

	public LinkAction(ClassNode inputNode) {
		super(inputNode, type.class);
	}

	@Override
	public String whatItDoes() {
		return "link";
	}

	@Override
	public void impl() throws Throwable {
		inputNode.link();
	}

	@Override
	public boolean applies() {
		return true;
	}

}
