package byransha;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class TextView<N extends BNode> extends View64<N> {

	@Override
	public byte[] content(N node, User u) {
		var sw = new StringWriter();
		var pw = new PrintWriter(sw);
		content(node, u, pw);
		return sw.getBuffer().toString().getBytes();
	}

	protected abstract void content(N node, User u, PrintWriter pw);

}
