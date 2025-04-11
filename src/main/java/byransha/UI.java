package byransha;

public class UI extends ValuedNode<Integer> {

	public UI(BBGraph db) {
		super(db);
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
