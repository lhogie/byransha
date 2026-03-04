package byransha.graph.view;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.relection.ClassNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.TextNode;

public class PlantUMLAction extends NodeAction<ListNode<ClassNode>, TextNode> {
	public PlantUMLAction(BGraph g, ListNode<ClassNode> node) {
		super(g, node);
	}

	@Override
	public String whatItDoes() {
		return "generate UML class diagram";
	}

	@Override
	public ActionResult<ListNode<ClassNode>, TextNode> exec() throws Throwable {
		return createResultNode(new TextNode(g, "PlantUML", ClassNode.toPlantUML(inputNode.get(), true)), true);
	}

}