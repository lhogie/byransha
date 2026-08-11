package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.primitive.DateNode;
import byransha.primitive.StringNode;

public class News extends LabNode {
	StringNode title = lookupOrCreate("title", id -> new StringNode(this, id, "", ".+"));
	StringNode text = lookupOrCreate("text", id -> new StringNode(this, id, null, ".+"));
	ImageNode image = lookupOrCreate("image", id -> new ImageNode(this, id));
	DateNode date = lookupOrCreate("date", id -> new DateNode(this, id));

	public News(Element parent, ID id) {
		super(parent, id);
	}
}
