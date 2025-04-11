package byransha;

public class BooleanNode extends ValuedNode<Boolean> {

	public BooleanNode(BBGraph db) {
		super(db);
	}

	public BooleanNode(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public String prettyName() {
		return "a boolean: " + get();
	}

	@Override
	public void fromString(String s) {
		set(Boolean.valueOf(s));
	}

	@Override
	public String toString() {
		return super.toString() + "(" + get() + ")";
	}

	public void set(Boolean newValue) {
		super.set(newValue);
	}

	public Boolean get() {
		return super.get();
	}

	@Override
	public String whatIsThis() {
		return "BooleanNode: " + get();
	}
}
