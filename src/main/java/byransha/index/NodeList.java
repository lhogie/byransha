package byransha.index;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import byransha.Element;
import byransha.util.Stop;

public class NodeList extends Index {
	public NodeList(AllIndexes allIndexes) {
		super(null);
		// super(allIndexes);
		// TODO Auto-generated constructor stub
	}

	final List<Element> l = new ArrayList<>();

	public Element forEachNode(Function<Element, Stop> f) {
		for (Element node : l) {
			if (f.apply(node) == Stop.yes) {
				return node;
			}
		}

		return null;
	}

	@Override
	public void add(Element n) {
		l.add(n);
	}

	@Override
	public void delete(Element n) {
		l.remove(n);
	}

	@Override
	public String strategy() {
		return "list of nodes";
	}

	public long size() {
		return l.size();
	}

	public Stream<Element> stream() {
		return l.stream();
	}
}