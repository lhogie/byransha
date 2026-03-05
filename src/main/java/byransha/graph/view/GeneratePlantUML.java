package byransha.graph.view;

import java.util.ArrayList;
import java.util.HashSet;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.relection.ClassNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.TextNode;

public class GeneratePlantUML<N extends BNode> extends NodeAction<ListNode<N>, TextNode> {
	public GeneratePlantUML(BGraph g, ListNode<N> node) {
		super(g, node);
		execStraightAway = true;
	}

	@Override
	public String whatItDoes() {
		return "generate UML class diagram";
	}

	@Override
	public ActionResult<ListNode<N>, TextNode> exec() throws Throwable {
		var l = new ArrayList<>(inputNode.get());
		var classNode = new HashSet<ClassNode>();

		for (var n : l) {
			classNode.add(n instanceof ClassNode cn ? cn : ClassNode.find(g, n.getClass()));
		}

		return createResultNode(new TextNode(g, "PlantUML", ClassNode.toPlantUML(classNode, true)), true);
	}

	@Override
	public boolean applies() {
		return inputNode.size() > 0;
	}

}