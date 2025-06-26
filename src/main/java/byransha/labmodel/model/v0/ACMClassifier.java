package byransha.labmodel.model.v0;

import byransha.BBGraph;

/*
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class ACMClassifier extends BusinessNode {
	public String code, descr;

	public ACMClassifier(BBGraph g, String code, String descr) {
		super(g);
		this.code = code;
		this.descr = descr;
	}

	public ACMClassifier(BBGraph g) {
		super(g);
	}

	public ACMClassifier(BBGraph g, int id) {
		super(g, id);
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
		return "a ACM classification";
	}

}
