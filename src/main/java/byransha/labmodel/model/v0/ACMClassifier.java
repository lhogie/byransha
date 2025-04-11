package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;

/*
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class ACMClassifier extends BNode {
	public String code, descr;

	public ACMClassifier(BBGraph g, String code, String descr) {
		super(g);
		this.code = code;
		this.descr = descr;
	}

	@Override
	public String prettyName() {
		return "ACM classification";
	}

	@Override
	public String toString() {
		return code + ": " + descr;
	}

	@Override
	public String whatIsThis() {
		return descr;
	}

}
