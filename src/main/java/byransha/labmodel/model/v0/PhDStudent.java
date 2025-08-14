package byransha.labmodel.model.v0;

import java.util.List;

import byransha.BBGraph;
import byransha.User;

public class PhDStudent extends Position {
	public PhDStudent(BBGraph g, User creator) {
		super(g, creator);
		endOfConstructor();
	}

	public PhDStudent(BBGraph g, User creator, int id) {
		super(g, creator, id);
		endOfConstructor();
	}

	List<Person> directors;
	Structure team;
}
