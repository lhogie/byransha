package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

/*
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class ACMClassifier extends BusinessNode {
	public String code, descr;

	public ACMClassifier(BBGraph g, String code, String descr, User user) {
		super(g, user);
		this.code = code;
		this.descr = descr;
	}

	public ACMClassifier(BBGraph g, User creator) {
		super(g, creator);
		endOfConstructor();
	}

	@Override
	protected void createOuts(User creator) {

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
