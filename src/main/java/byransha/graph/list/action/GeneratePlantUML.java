package byransha.graph.list.action;

import java.util.ArrayList;
import java.util.HashSet;

import byransha.graph.BNode;
import byransha.graph.Category.export;
import byransha.graph.Category.list;
import byransha.graph.relection.ClassNode;
import byransha.nodes.primitive.TextNode;

public class GeneratePlantUML<N extends BNode> extends FunctionAction<ListNode<N>, TextNode> {
	public GeneratePlantUML(ListNode<N> node) {
		super(node, list.class, export.class);
	}

	@Override
	public String whatItDoes() {
		return "generate UML class diagram";
	}

	@Override
	public void impl() throws Throwable {
		var l = new ArrayList<>(inputNode.get());
		var classNode = new HashSet<ClassNode>();

		for (var n : l) {
			classNode.add(n instanceof ClassNode cn ? cn : g().indexes.byClass.getClassNodeFor(n.getClass()));
		}

		result = new TextNode(null, "PlantUML", ClassNode.toPlantUML(classNode, true));
	}

	@Override
	public boolean applies() {
		return !inputNode.elements.isEmpty();
	}

}