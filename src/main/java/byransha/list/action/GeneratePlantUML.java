package byransha.list.action;

import java.util.ArrayList;
import java.util.HashSet;

import byransha.Element;
import byransha.action.Category.export;
import byransha.action.Category.list;
import byransha.graph.relection.ClassNode;
import byransha.primitive.TextNode;

public class GeneratePlantUML<N extends Element> extends FunctionAction<ListNode<N>, TextNode> {
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
			classNode.add(n instanceof ClassNode cn ? cn : hub().indexes.byClass.getClassNodeFor(n.getClass()));
		}

		result = new TextNode(this, null, "PlantUML", ClassNode.toPlantUML(classNode, true));
	}

	@Override
	public boolean applies() {
		return !inputNode.elements.isEmpty();
	}

}