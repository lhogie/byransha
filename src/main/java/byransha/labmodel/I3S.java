package byransha.labmodel;

import byransha.BBGraph;
import byransha.BNode;
import byransha.StringNode;
import byransha.User;
import byransha.labmodel.model.v0.Lab;

/*
 * https://codimd.math.cnrs.fr/_ivy9aRUQK2o4ue-p9RHKg?both
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class I3S extends Lab {

	public I3S(BBGraph g, User creator) {
		super(g, creator);
		name = new StringNode(g, creator);
		name.set("I3S", creator);
	}

	public I3S(BBGraph g, User creator, int id) {
		super(g, creator, id);
	}

}
