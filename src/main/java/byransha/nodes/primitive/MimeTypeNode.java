package byransha.nodes.primitive;

import java.util.ArrayList;
import java.util.List;

import byransha.graph.BBGraph;
import byransha.graph.NodeError;
import byransha.nodes.system.User;

public class MimeTypeNode extends StringNode {

	public static final List<String> validMimeTypes = new ArrayList<String>();

	public MimeTypeNode(BBGraph g, User creator) {
		super(g, creator);
	}

	@Override
	protected void fillErrors(List<NodeError> errs) {
		super.fillErrors(errs);
		String v = get();

		if (!validMimeTypes.contains(v))
			errs.add(new NodeError(this, "invalid MIME type. Valid values are " + validMimeTypes));
	}
}
