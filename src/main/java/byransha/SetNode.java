package byransha;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class SetNode<N extends BNode> extends BNode {

	@Override
	public String whatIsThis() {
		return "SetNode containing " + l.size() + " elements.";
	}

	public SetNode(BBGraph db) {
		super(db);
	}

	public SetNode(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public String prettyName() {
		return "a set";
	}

	private final Set<N> l = ConcurrentHashMap.newKeySet();

	@Override
	public void forEachOut(BiConsumer<String, BNode> consumer) {
		if(l == null) {
			return;
		}
		for (var e : l) {
			if (e != null) {
				consumer.accept("" + e.id(), e);
			}
		}
	}

	public void add(N n) {
		l.add(n);
	}

	public void remove(N p) {
		l.remove(p);
	}
}
