package byransha;

public abstract class BusinessElement extends Element {

	public BusinessElement(InstantiationParameters p) {
		super(p);
	}

	public BusinessElement(Element parent, ID id) {
		super(parent, id);
	}
}
