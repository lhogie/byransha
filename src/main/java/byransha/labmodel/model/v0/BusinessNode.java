package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.ValueHistoryEntry;

import java.time.OffsetDateTime;

public abstract class BusinessNode extends BNode {

	public BusinessNode(BBGraph g, User creator) {
		super(g, creator);
	}

	public BusinessNode(BBGraph g, int id) {
		super(g, id);
	}

}
