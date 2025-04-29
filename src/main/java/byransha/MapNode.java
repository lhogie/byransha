package byransha;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

public class MapNode<N extends BNode> extends BNode {
	public MapNode(BBGraph db) {
		super(db);
	}
	@Override
	public String prettyName() {
		return "a map";
	}
	@Override
	public String whatIsThis() {
		return "MapNode with " + l.size() + " entries";
	}

	private final ConcurrentMap<String, N> l = new ConcurrentHashMap<>();

	@Override
	public void forEachOut(BiConsumer<String, BNode> consumer) {
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
