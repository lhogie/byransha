package byransha.graph;

public abstract class ServiceNode extends BNode {

	public ServiceNode(BNode parent) {
		super(parent);
		super.setID(getClass().hashCode());
	}

	@Override
	public void setID(long newID) {
		throw new UnsupportedOperationException("ServiceNode ID is fixed");
	};

}
