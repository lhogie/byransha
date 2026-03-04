package byransha.graph;

public abstract class Index extends BNodeNOTNODE implements GraphListener {
	public Index(BGraph g) {
		super(g);
	}

	public abstract void add(BNode n);

	public abstract void idChanged(int oldID, int newID);

	public abstract void delete(BNode n);

	public abstract void arcDeleted(BNode from, BNode to);

	public abstract void arcAdded(BNode from, BNode to);
	
	public abstract String strategy();

	@Override
	public String whatIsThis() {
		return "a graph data structure";
	}

	@Override
	public String prettyName() {
		return "graph data structure (" + strategy() + ")";
	}
}