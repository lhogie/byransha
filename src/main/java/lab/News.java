package lab;

import byransha.InstantiationParameters;
import byransha.primitive.DateNode;
import byransha.primitive.ImageElement;
import byransha.primitive.StringNode;

public class News extends LabElement {
	StringNode title = fieldNode("title", id -> new StringNode(this, id, "", ".+"));
	StringNode text = fieldNode("text", id -> new StringNode(this, id, null, ".+"));
	ImageElement image = fieldNode("image", id -> new ImageElement(this, id));
	DateNode date = fieldNode("date", id -> new DateNode(this, id));

	public News(InstantiationParameters p) {
		super(p);
	}
}
