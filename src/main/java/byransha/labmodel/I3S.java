package byransha.labmodel;

import byransha.BBGraph;
import byransha.StringNode;
import byransha.User;
import byransha.labmodel.model.v0.Lab;

/*
 * https://codimd.math.cnrs.fr/_ivy9aRUQK2o4ue-p9RHKg?both
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class I3S extends Lab {

	public I3S(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		endOfConstructor();
	}

	@Override
	protected void createOuts(User creator) {
		super.createOuts(creator);
		name = new StringNode(g, creator, InstantiationInfo.persisting);
		name.set("I3S", creator);
	}
}
