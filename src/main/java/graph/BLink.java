package graph;

public class BLink extends BGElement {

	final public  BVertex source;
	final public BVertex target;
	 public String arrowType;
	 public int distance = 50;

	public BLink(BVertex v, BVertex w) {
		this.source = v;
		this.target = w;
	}

}
