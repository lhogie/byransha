package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.HistoryChangeNode;

public abstract class BusinessNode extends BNode {
	public HistoryChangeNode history;

	public BusinessNode(BBGraph g) {
		super(g);
		history = g.create( HistoryChangeNode.class);
	}

	public BusinessNode(BBGraph g, int id) {
		super(g, id);
	}

}
