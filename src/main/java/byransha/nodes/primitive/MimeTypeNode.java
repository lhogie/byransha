package byransha.nodes.primitive;

import java.util.ArrayList;
import java.util.List;

import byransha.graph.BBGraph;
import byransha.graph.NodeError;

public class MimeTypeNode extends StringNode {

	public static final List<String> validMimeTypes = new ArrayList<String>();

	static {
		validMimeTypes.add("image/jpeg");
		validMimeTypes.add("application/pdf");
	}

	public MimeTypeNode(BBGraph g) {
		super(g);
	}

	@Override
	protected void fillErrors(List<NodeError> errs) {
		String v = get();

		if (!validMimeTypes.contains(v))
			errs.add(new NodeError(this, "invalid MIME type. Valid values are " + validMimeTypes));
	}
}
