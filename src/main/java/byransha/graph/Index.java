package byransha.graph;

public abstract class Index  {
	
	public abstract void add(BNode n);

	public abstract void delete(BNode n);
	
	public abstract String strategy();

}