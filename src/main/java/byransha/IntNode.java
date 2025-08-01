package byransha;

public class IntNode extends ValuedNode<Integer> {

	public IntNode(BBGraph db) {
		super(db);
	}
    public IntNode(BBGraph db, int id) {
        super(db, id);
    }

	@Override
	public String prettyName() {
		return "an integer";
	}

	@Override
	public void fromString(String s) {
		set(Integer.valueOf(s));
	}

	@Override
	public String whatIsThis() {
		return "IntNode with value: " + get();
	}
}
