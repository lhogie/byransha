package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.nodes.system.User;

/*
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class ACMClassifier extends BusinessNode {
	public String code, descr;


	public ACMClassifier(BBGraph g, User creator) {
		super(g, creator);
	}

	@Override
	public String toString() {
		return prettyName();
	}


	@Override
	public String prettyName() {
		return code + ": " + descr;
	}


	@Override
	public String whatIsThis() {
		return "a ACM classification";
	}

}
