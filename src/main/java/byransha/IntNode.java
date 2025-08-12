package byransha;

public class IntNode extends PrimitiveValueNode<Integer> {

	public IntNode(BBGraph db, User creator) {
		super(db, creator);
	}
    public IntNode(BBGraph db, int id) {
        super(db, id);
    }

	@Override
	public String prettyName() {
		return "an integer";
	}

	@Override
	public void fromString(String s, User user) {
		set(Integer.valueOf(s), user);
	}

	@Override
	public String whatIsThis() {
		return "IntNode with value: " + get();
	}
}
