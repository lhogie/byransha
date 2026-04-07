package byransha.nodes.primitive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import byransha.graph.BGraph;
import byransha.graph.view.TextNodeView;

public class TextNode extends PrimitiveValueNode<String> {
	StringNode labelNode;
	public boolean info;

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
	public String whatIsThis() {
		return "a multiline text";
	}

	@Override
	public String defaultValue() {
		return null;
	}
	
	@Override
	protected void writeValue(String v, ObjectOutput out) throws IOException {
		out.writeUTF(v);
	}

	@Override
	protected String readValue(ObjectInput in) throws IOException {
		return in.readUTF();
	}
}
