package byransha.nodes.lab.model.v0;

import java.util.List;

import byransha.BBGraph;
import byransha.nodes.system.User;

public class PhDStudent extends Position {
	public PhDStudent(BBGraph g, User creator) {
		super(g, creator);
	}

	List<Person> directors;
	Structure team;
}
