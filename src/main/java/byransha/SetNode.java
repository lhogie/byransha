package byransha;

import javassist.bytecode.analysis.SubroutineScanner;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class SetNode<N extends BNode> extends PersistingNode {
	public boolean canAddNewNode = true;
	public boolean isDropdown = false;

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
		int i = 0;
		for (N e : l) {
			if (e != null) {
				consumer.accept(i++ + ". " + e.prettyName(), e);
			} else {
				i++;
			}
		}
	}

	public void add(N n) {
		l.add(n);
		this.save(f -> {});
	}

	public void disableAddNewNode(){
		canAddNewNode = false;
	}

	public void enableIsDropdown() {
		canAddNewNode = false;
		isDropdown = true;
	}

	public void remove(N p) {
		l.remove(p);
	}
}
