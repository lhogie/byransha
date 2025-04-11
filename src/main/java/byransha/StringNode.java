package byransha;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StringNode extends ValuedNode<String> {

	private BNode labelFor;

	public StringNode(BBGraph g, String init) {
		super(g);
		set(init);
	}

	public StringNode(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public String prettyName() {
		return "a string: " + get();
	}

	@Override
	public void fromString(String s) {
		set(s);
	}

	@Override
	public void set(String newValue) {
		super.set(newValue);

		if (false)// labelFor != null)
		{
			File target = labelFor.directory();
			String oldValue = null;
			File oldLink = oldValue == null ? null : new File(target.getParentFile(), oldValue);
			File newLink = newValue == null ? null : new File(target.getParentFile(), newValue);

			if (newLink != null && oldLink != null) {
				if (!newLink.exists()) {
					try {
						Files.createSymbolicLink(newLink.toPath(), target.toPath());
					} catch (IOException err) {
						throw new IllegalStateException(err);
					}
				}

			}
		}

	}

	@Override
	public String whatIsThis() {
		return "StringNode: " + get();
	}

	public void setAsLabelFor(BNode n) {
		labelFor = n;
	}
}
