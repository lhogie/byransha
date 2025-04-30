package byransha;

import java.util.Base64;

public class ImageNode extends ValuedNode<byte[]> {

	final StringNode title;

	public ImageNode(BBGraph db) {
		super(db);
		title = db.addNode(StringNode.class);
	}

	public ImageNode(BBGraph db, int id) {
		super(db, id);
		title = db.addNode(StringNode.class);
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
