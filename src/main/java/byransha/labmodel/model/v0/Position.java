package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.DateNode;
import byransha.User;

public class Position extends BNode {
	Structure employer;
	DateNode from;
	DateNode to;
	Status status;

	public Position(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		endOfConstructor();
	}

	@Override
	protected void createOuts(User creator) {
		super.createOuts(creator);
		employer = new Structure(g, creator, InstantiationInfo.persisting);
		from = new DateNode(g, creator, InstantiationInfo.persisting);
		to = new DateNode(g, creator, InstantiationInfo.persisting);
		status = new Status(g, creator, InstantiationInfo.persisting);
	}

	@Override
	public String prettyName() {
		if(status != null) return "Position with status: " + status.prettyName();
		return null;
	}

	@Override
	public String whatIsThis() {
		return "Position node in the graph";
	}

}
