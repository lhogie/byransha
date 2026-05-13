package byransha.nodes.lab;

import byransha.graph.BNode;
import byransha.nodes.primitive.DateNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.URLNode;

public class News extends BNode {
	StringNode title = new StringNode(this);
	StringNode text = new StringNode(this);
	URLNode image = new URLNode(this, "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg");
	DateNode date = new DateNode(this);

	public News(BNode parent) {
		super(parent);
	}
}
