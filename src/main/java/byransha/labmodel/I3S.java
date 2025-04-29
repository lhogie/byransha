package byransha.labmodel;

import byransha.BBGraph;
import byransha.StringNode;
import byransha.labmodel.model.v0.Lab;

/*
 * https://codimd.math.cnrs.fr/_ivy9aRUQK2o4ue-p9RHKg?both
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class I3S extends Lab {

	public I3S(BBGraph g) {
		super(g);
		name =(StringNode) g.addNode(StringNode.class);
		name.set("I3S");
	}

	public I3S(BBGraph g, int id) {
		super(g, id);
	}

}
