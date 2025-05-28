package byransha;

import java.util.Base64;

public class ImageNode extends ValuedNode<byte[]> {

	public StringNode title;

	public ImageNode(BBGraph g) {
		super(g);
		title = BNode.create(g, StringNode.class);
	}

	public ImageNode(BBGraph g, int id) {
		super(g, id);
		title = BNode.create(g, StringNode.class);
	}

	@Override
	public String prettyName() {
		return title.get();
	}

	@Override
	public void fromString(String s) {
		set(Base64.getDecoder().decode(s.getBytes()));
	}

	@Override
	public String getAsString() {
		return Base64.getEncoder().encodeToString(get());
	}

	@Override
	public String whatIsThis() {
		return "an image";
	}
}
