package byransha.lab;

import byransha.graph.BNode;
import byransha.primitive.DateNode;
import byransha.primitive.StringNode;
import byransha.primitive.URLNode;

public class News extends BNode {
	StringNode title = new StringNode(this);
	StringNode text = new StringNode(this);
	URLNode image = new URLNode(this, "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg");
	DateNode date = new DateNode(this);

	public News(BNode parent) {
		super(parent);
	}
}
