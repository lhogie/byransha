package byransha;

public abstract class Application extends Element {

	public Application(Element parent, ID id) {
		super(parent, id);
	}

	public abstract Class<? extends BusinessElement> businessClass();
}
