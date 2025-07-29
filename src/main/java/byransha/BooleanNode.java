package byransha;

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

	@Override
	public <N extends BNode> N set(Boolean newValue) {
		this.value = newValue;
		if (directory() != null) {
			saveValue(BBGraph.sysoutPrinter);
		}
		return null;
	}

	public void setName(String name) {
		this.name = name;
	}
}
