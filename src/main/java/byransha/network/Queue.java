package byransha.network;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import byransha.graph.ActionMethod;
import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;

public class Queue<E> extends BNode {
	public final BlockingQueue<E> q = new ArrayBlockingQueue<>(100);

	protected Queue(BNode parent) {
		super(parent);
	}

	@ShowInKishanView
	public int remainingCapacity() {
		return q.remainingCapacity();
	}

	@ShowInKishanView
	public int size() {
		return q.size();
	}

	@ActionMethod
	public void clear() {
		q.clear();
	}
}
