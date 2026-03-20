package byransha.nodes.primitive;

import byransha.graph.BGraph;
import byransha.graph.view.TextNodeView;

public class TextNode extends PrimitiveValueNode<String> {
	StringNode labelNode;

	public TextNode(BGraph g, String label, String data) {
		super(g);
		set(data);
		labelNode = new StringNode(g, label, ".+");
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new TextNodeView(g, this));
		super.createViews();
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new saveNodeAction(g, this));
		super.createActions();
	}

	@Override
	public String prettyName() {
		return labelNode.get();
	}

	@Override
	public String valueFromString(String s) {
		return s.replaceAll("\\n", "\n");
	}

	@Override
	public String getValueAsString() {
		return super.getValueAsString().replace("\n", "\\n");
	}

	@Override
	public String whatIsThis() {
		return "a multiline text";
	}

	@Override
	public String defaultValue() {
		return null;
	}
}
