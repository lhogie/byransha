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
		return "boolean: " + get();
	}

	@Override
	public void fromString(String s) {
		set(Boolean.valueOf(s));
	}

	@Override
	public String whatIsThis() {
		return "a boolean";
	}
}
