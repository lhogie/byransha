package byransha.nodes.primitive;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class MapNode<N extends BNode> extends BNode {
	public final ConcurrentMap<String, N> map = new ConcurrentHashMap<>();
	private String label;

	public MapNode(BGraph g, String label) {
		super(g);
		this.label = label;
	}

	@Override
	public String prettyName() {
		return "a map";
	}

	@Override
	public String whatIsThis() {
		return "a map";
	}

	@Override
	public void forEachOut(BiConsumer<BNode, String> consumer) {
		for (Map.Entry<String, N> e : map.entrySet()) {
			if (e.getValue() != null) {
				consumer.accept(e.getValue(), e.getKey());
			}
		}
	}

	public void add(String key, N n) {
		map.put(key, n);
	}

	public void remove(String key) {
		map.remove(key);
	}

}
