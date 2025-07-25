package byransha;

public class BooleanNode extends ValuedNode<Boolean> {
	public String name = "boolean";
	public BNode nodeToSetVisible;

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

	@Override
	public void set(Boolean newValue) {
		this.value = newValue;
		if(nodeToSetVisible != null) {
			nodeToSetVisible.isVisible = newValue;
			if(!newValue && nodeToSetVisible instanceof ValuedNode<?>) {
				((ValuedNode<?>) nodeToSetVisible).set(null);
			}
		}
		if (directory() != null) {
			saveValue(BBGraph.sysoutPrinter);
		}
	}

	public void setNodeToSetVisible(BNode nodeToSetVisible) {
		this.nodeToSetVisible = nodeToSetVisible;
		this.nodeToSetVisible.isVisible = this.get();
	}

	public void setName(String name) {
		this.name = name;
	}
}
