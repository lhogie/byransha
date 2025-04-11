package byransha;

import java.util.Base64;

public class ImageNode extends ValuedNode<byte[]> {

	StringNode title;

	public ImageNode(BBGraph db) {
		super(db);
		title = new StringNode(db, null);
	}

	@Override
	protected String prettyName() {
		return title.get();
	}

	@Override
	public void fromString(String s) {
		set(Base64.getDecoder().decode(s.getBytes()));
	}

	@Override
	public String whatIsThis() {
		return "an image";
	}
}
