package byransha.graph.relection;

import byransha.event.NewNodeEvent;
import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.relection.LinkAction.type;
import byransha.primitive.BooleanNode;

public class NewInstance extends FunctionAction<ClassNode, BNode> {
	// @ShowInKishanView
	BNode parent = super.parent;

	@ShowInKishanView
	BooleanNode global = new BooleanNode(this, true);

	public NewInstance(ClassNode inputNode) {
		super(inputNode, type.class);
	}

	@Override
	public String whatItDoes() {
		return "creates a new instance";
	}

	@Override
	public void impl() {
		result = inputNode.newInstance(parent);

		if (result.generateEvents = global.get()) {
			var e = new NewNodeEvent(result);
			hub().eventList.add(e);
		}
	}

	@Override
	public boolean applies() {
		return true;
	}
}
