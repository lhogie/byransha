package byransha.nodes.primitive;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
		cachedViews.add(new TextNodeView(g, this));
	}

	@Override
	public void createActions() {
		cachedActions.add(new saveNodeAction(g, this));
	}

	@Override
	protected byte[] valueToBytes(String s) throws IOException {
		return s.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	protected String bytesToValue(byte[] bytes) throws IOException {
		return new String(bytes, StandardCharsets.UTF_8);
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
