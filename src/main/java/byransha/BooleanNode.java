package byransha;

import java.lang.reflect.Field;

public class BooleanNode extends ValuedNode<Boolean> {
	public String name = "boolean";

	public BooleanNode(BBGraph db) {
		super(db);
	}

	public BooleanNode(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public String prettyName() {
		return name + " : " + (get() == null ? "null" : get().toString());
	}

	@Override
	public void fromString(String s) {
		set(null, null, Boolean.valueOf(s));
	}

	@Override
	public String whatIsThis() {
		return "a boolean with name : " + name;
	}

	@Override
	public void set(Boolean newValue) {
		throw  new UnsupportedOperationException("Cannot use set(Boolean) on BooleanNode directly. Use set(Field, BNode, Boolean) instead.");
	}

	public void set(String fieldName, BNode parentNode, Boolean newValue) {
		BooleanNode node = graph.find(BooleanNode.class, n -> {return n.get()!=null && n.get().equals(newValue);});
		if(node != null) parentNode.setField(fieldName, node);
		else super.set(newValue);
	}

	public void setName(String name) {
		this.name = name;
	}
}
