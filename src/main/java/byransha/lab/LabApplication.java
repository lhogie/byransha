package byransha.lab;

import byransha.Application;
import byransha.Element;
import byransha.ID;
import byransha.lab.Genre.Female;
import byransha.lab.Genre.Male;
import byransha.lab.Genre.NotGenred;

public class LabApplication extends Application {

	public final Genre male = fieldNode("male", id -> new Male(this, id));
	public final Genre female = fieldNode("male", id -> new Female(this, id));
	public final Genre notgenred = fieldNode("male", id -> new NotGenred(this, id));

	public final Status dr = fieldNode("DR", id -> new DR(this, id));
	public final Status ir = fieldNode("IR", id -> new IR(this, id));
	public final Status igr = fieldNode("IGR", id -> new IGR(this, id));
	public final Status mcf = fieldNode("MCF", id -> new MCF(this, id));
	public final Status prof = fieldNode("Prof", id -> new Prof(this, id));

	public final Lab i3s = fieldNode("i3s", id -> new I3S(this, id));

	public LabApplication(Element parent) {
		super(parent, ID.fromBase62("FDS"));
	}

	@Override
	public Class businessClass() {
		return LabElement.class;
	}
}
