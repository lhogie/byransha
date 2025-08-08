package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.HistoryChangeNode;
import byransha.PersistingNode;

public abstract class BusinessNode extends PersistingNode {
	public HistoryChangeNode history;

	public BusinessNode(BBGraph g) {
		super(g);
		history = g.create( HistoryChangeNode.class);
	}

	public BusinessNode(BBGraph g, int id) {
		super(g, id);
	}

}
