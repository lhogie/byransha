package byransha.index;

import byransha.Element;

public abstract class Index {

	protected Index(Element parent) {
		// super(parent);
		// TODO Auto-generated constructor stub
	}

	public abstract void add(Element n);

	public abstract void delete(Element n);

	public abstract String strategy();

}