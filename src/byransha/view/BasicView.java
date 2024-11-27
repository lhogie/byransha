package byransha.view;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.GOBMNode;
import byransha.User;
import byransha.View;

final public class BasicView extends View<GOBMNode> {
	@Override
	public byte[] content(GOBMNode nn, User u) {
		var n = new ArrayNode(null);
		{
			var o = new ObjectNode(null);
			o.set("class", new TextNode(getClass().getName()));
			n.add(o);
		}
		{
			var o = new ObjectNode(null);
			o.set("out degree", new IntNode(nn.outDegree()));
			n.add(o);
		}
		return n.toString().getBytes();
	}

	@Override
	protected String contentType() {
		return "text/json";
	}
	
	
	@Override
	public String name() {
		return "basic information";
	}
	
}