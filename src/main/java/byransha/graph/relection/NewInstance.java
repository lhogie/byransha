package byransha.graph.relection;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.relection.LinkAction.type;
import byransha.primitive.BooleanNode;

public class NewInstance extends FunctionAction<ClassNode, Element> {
	// @ShowInKishanView
	Element parent = super.parent;

	@ShowInKishanView
	BooleanNode shareWithOtherNodes = new BooleanNode(this, null, true);

	public NewInstance(ClassNode inputNode) {
		super(inputNode, type.class);
	}

	@Override
	public String whatItDoes() {
		return "creates a new instance";
	}

	@Override
	public void impl() {
		ID id = shareWithOtherNodes.get() ? new ID() : null;
		result = inputNode.newInstance(parent, id);
	}

	@Override
	public boolean applies() {
		return true;
	}
}
