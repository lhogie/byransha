package byransha;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

public class MapNode<N extends BNode> extends BNode {
	public MapNode(BBGraph g, User creator) {
		super(g, creator);
		endOfConstructor();
	}

	@Override
	protected void createOuts(User creator) {

	}
	@Override
	public String prettyName() {
		return "a map";
	}

	@Override
	public String whatIsThis() {
		return "a map";
	}

	private final ConcurrentMap<String, N> l = new ConcurrentHashMap<>();

	@Override
	public void forEachOutField(BiConsumer<String, BNode> consumer) {
		for (Map.Entry<String, N> e : l.entrySet()) {
			if (e.getValue() != null) {
				consumer.accept(e.getKey(), e.getValue());
			}
		}
	}

	public void add(String key, N n) {
		l.put(key, n);
	}

	public void remove(String key) {
		l.remove(key);
	}

}
