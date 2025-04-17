package byransha.labmodel.model.v0;

import java.util.List;

import byransha.BBGraph;

public class PhDStudent extends Position {
	public PhDStudent(BBGraph g) {
		super(g);
	}

	public PhDStudent(BBGraph g, int id) {
		super(g, id);
	}

	List<Person> directors;
	Structure team;
}
