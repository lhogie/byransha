package byransha;

public class BooleanNode extends ValuedNode<Boolean> {
	public String name = "boolean";

	public BooleanNode(BBGraph db) {
		super(db);
		set(false);
	}

	public BooleanNode(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public String prettyName() {
		return name;
	}

	@Override
	public void fromString(String s) {
		set(Boolean.valueOf(s));
	}

	@Override
	public String whatIsThis() {
		return "a boolean with name : " + name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
