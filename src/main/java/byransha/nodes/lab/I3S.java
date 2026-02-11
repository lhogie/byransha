package byransha.nodes.lab;

import byransha.BBGraph;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;
import byransha.nodes.lab.model.v0.Lab;

/*
 * https://codimd.math.cnrs.fr/_ivy9aRUQK2o4ue-p9RHKg?both
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class I3S extends Lab {

	public I3S(BBGraph g, User creator) {
		super(g, creator);
		name = new StringNode(g, creator, "I3S");
		name.set("I3S", creator);
	}
}
