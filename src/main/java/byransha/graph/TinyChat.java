package byransha.graph;

import java.io.IOException;

import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.SystemNode;

public class TinyChat extends SystemNode {
	@ShowInKishanView
	public final StringNode input = new StringNode(this);

	public TinyChat(BGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a tiny chat";
	}

	@ActionMethod
	@AddButtonOnKishanView
	public void send() {
		try {
			g().networkAgent.sendObject(input.get());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
