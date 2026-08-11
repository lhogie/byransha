package byransha.primitive;

import java.util.ArrayList;
import java.util.List;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.NodeError;

public class MimeTypeNode extends StringNode {

	public static final List<String> validMimeTypes = new ArrayList<String>();

	static {
		validMimeTypes.add("image/jpeg");
		validMimeTypes.add("application/pdf");
	}

	public MimeTypeNode(Element g, ID id) {
		super(g, id, "", "([a-z]+)/([a-z]+)");
	}

	@Override
	protected void fillErrors(List<NodeError> errs) {
		String v = get();

		if (!validMimeTypes.contains(v))
			errs.add(new NodeError(this, "invalid MIME type. Valid values are " + validMimeTypes));
	}
}
