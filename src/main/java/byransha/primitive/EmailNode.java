package byransha.primitive;

import java.util.List;

import byransha.ID;
import byransha.ProblemInElement;
import byransha.lab.Person;

public class EmailNode extends StringNode {

	public static final String re = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

	public EmailNode(Person g, String s, ID id) {
		super(g, id, s, re);
	}

	public Person owner() {
		return (Person) parent;
	}

	@Override
	protected void fillErrors(List<ProblemInElement> errs) {
		if (!get().matches(re)) {
			errs.add(new ProblemInElement(this, "invalid email address"));
		}
	}
}
