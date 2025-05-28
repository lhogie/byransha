package byransha.graph;

public class BVertex extends BGElement {
	public final String id;
	public int size = 10;
	public String prettyName;
	public String whatIsThis;
	public String className;
	
	public BVertex(String id) {
		this.id = id;
	}
}
