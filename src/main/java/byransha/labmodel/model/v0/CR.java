package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class CR extends Status {
	public CR(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		name.set("Chargé de Recherche", creator);
		endOfConstructor();
	}
}
