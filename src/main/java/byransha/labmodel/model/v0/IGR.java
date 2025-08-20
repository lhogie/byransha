package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class IGR extends Status {
	public IGR(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		name.set("Ingénieur de Recherche Université", creator);
		endOfConstructor();
	}
}
