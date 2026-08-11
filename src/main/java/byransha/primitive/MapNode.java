package byransha.primitive;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

import byransha.ID;
import byransha.graph.Element;

public class MapNode<N extends Element> extends Element {
	public final ConcurrentMap<String, N> map = new ConcurrentHashMap<>();
	private String label;

	public MapNode(Element parent, ID id, String label) {
		super(parent, id);
		this.label = label;
	}

	@Override
	public String toString() {
		return "a map";
	}

	@Override
	public String whatIsThis() {
		return "a map";
	}

	@Override
	public void forEachOut(BiConsumer<Element, String> consumer) {
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
