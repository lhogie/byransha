package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

/*
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class ACMClassifier extends BusinessNode {
	public String code, descr;

	public ACMClassifier(BBGraph g, String code, InstantiationInfo ii, String descr, User user) {
		super(g, user, ii);
		this.code = code;
		this.descr = descr;
	}

	public ACMClassifier(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
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
