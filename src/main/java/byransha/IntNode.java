package byransha;

public class IntNode extends PrimitiveValueNode<Integer> {

	public IntNode(BBGraph db, User creator) {
		super(db, creator);
		endOfConstructor();
	}
    public IntNode(BBGraph db, User creator, int id) {
		super(db, creator);
		endOfConstructor();
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
