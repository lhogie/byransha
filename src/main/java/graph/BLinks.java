package main.java.graph;

public class BLinks extends BGElement {

	final public  BVertex source;
	final public BVertex target;
	 public String arrowType;
	 public int distance = 50;

	public BLinks(BVertex v, BVertex w) {
		this.source = v;
		this.target = w;
	}

}
