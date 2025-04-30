package byransha;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class ListNode<N extends BNode> extends PersistingNode {
	public final List<N> l = new CopyOnWriteArrayList<>();

	public ListNode(BBGraph db) {
		super(db);
	}

	public ListNode(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public String whatIsThis() {
		return "ListNode containing " + l.size() + " elements.";
	}

	@Override
	public String prettyName() {
		return "a list";
	}

	@Override
	public void forEachOut(BiConsumer<String, BNode> consumer) {
		int i = 0;
		for (N e : l) {
			if (e != null) {
				consumer.accept(i++ + ". " + e.id(), e);
			} else {
				i++;
			}
		}
	}

	public void add(N n) {
		l.add(n);
	}

	public void remove(N p) {
		l.remove(p);
	}

	public N get(int i) {
		return l.get(i);
	}

	public List<N> elements() {
		return List.copyOf(l);
	}

	public int size() {
		return l.size();
	}

	public BNode random() {
		int currentSize = l.size();
		if (currentSize == 0) {
			return null;
		}
		return l.get(new Random().nextInt(currentSize));
	}
}
