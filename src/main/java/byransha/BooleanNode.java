package byransha;

public class BooleanNode extends ValuedNode<Boolean> {

	public BooleanNode(BBGraph db) {
		super(db);
	}
	@Override
	protected String prettyName() {
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
