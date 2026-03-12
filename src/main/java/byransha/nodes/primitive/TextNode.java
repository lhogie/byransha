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
		cachedViews.values.add(new TextNodeView(g, this));
		super.createViews();
	}

	@Override
	public void createActions() {
		cachedActions.values.add(new saveNodeAction(g, this));
		super.createActions();
	}

	@Override
	public String prettyName() {
		return "text";
	}

	@Override
	public void fromString(String s) {
		set(s);
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
