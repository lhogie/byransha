package byransha.nodes.primitive;

import java.time.LocalDateTime;
import java.util.Iterator;

import byransha.event.Event;
import byransha.graph.BGraph;

public class ValuedNodeValueChangeEvent<V> extends Event {
	int nodeID;
	V oldValue;
	V newValue;

	public ValuedNodeValueChangeEvent(BGraph g, LocalDateTime date, int nodeID, V oldValue, V newValue) {
		super(g, date);
		this.nodeID = nodeID;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public void apply(BGraph g) throws Throwable {
		((ValuedNode<V>) g.indexes.byId.get(nodeID)).set(newValue);
	}

	@Override
	public void undo(BGraph g) throws Throwable {
		((ValuedNode<V>) g.indexes.byId.get(nodeID)).set(oldValue);
	}

	@Override
	protected void fromCSV(Iterator<String> elementIterator, BGraph g) {
		this.nodeID = Integer.valueOf(elementIterator.next());
		var n = ((ValuedNode<V>) g.indexes.byId.get(nodeID));
		this.oldValue = (V) n.valueFromString(elementIterator.next());
		this.newValue = (V) n.valueFromString(elementIterator.next());
	}

}