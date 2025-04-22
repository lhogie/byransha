package byransha;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ListNode<N extends BNode> extends PersistingNode {
	public final List<N> l = new ArrayList<>();

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

		if (l != null) {
			for (var e : l) {
				consumer.accept(i++ + ". " + e.id(), e);
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
		return l.get(new Random().nextInt(l.size()));
	}

	public void saveAll(Consumer<File> consumer) {
		createOutSymLinks(consumer);
		createInSymLinks(consumer);
	}
}
