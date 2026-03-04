package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.nodes.system.User;

/*
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class ACMClassifier extends BusinessNode {
	public String code, descr;


	public ACMClassifier(BGraph g) {
		super(g);
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
