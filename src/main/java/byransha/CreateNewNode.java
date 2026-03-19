package byransha;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.function.Consumer;

import byransha.event.Event;
import byransha.graph.BGraph;
import byransha.graph.BNode;

public class CreateNewNode<N extends BNode> extends Event {

	Class<N> clazz;
	int id = -1;

	public CreateNewNode(BGraph g, LocalDateTime date) {
		super(g, date);
	}

	@Override
	public void undo(BGraph g) throws Throwable {
		g.indexes.byId.get(id).delete();
	}

	@Override
	public void apply(BGraph g) throws Throwable {
		var n = clazz.getConstructor(BGraph.class).newInstance(g);

		if (id != -1)
			g.indexes.byId.forceIndex(n, id);
	}

	@Override
	public void fromCSV(Iterator<String> l, BGraph g) throws ClassNotFoundException {
		this.clazz = (Class<N>) Class.forName(l.next());
		this.id = Integer.valueOf(id);
	}

	@Override
	public void provideCSVElements(Consumer<String> l) {
		super.provideCSVElements(l);
		l.accept(clazz.getName());
		l.accept(id + "");
	}

}