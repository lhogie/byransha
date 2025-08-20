package byransha.labmodel.model.v0;

import java.util.List;

import byransha.BBGraph;
import byransha.User;

public class PhDStudent extends Position {
	public PhDStudent(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		endOfConstructor();
	}

	List<Person> directors;
	Structure team;
}
