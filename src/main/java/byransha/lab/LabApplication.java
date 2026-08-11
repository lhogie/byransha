package byransha.lab;

import byransha.Application;
import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.lab.Genre.Female;
import byransha.lab.Genre.Male;
import byransha.lab.Genre.NotGenred;

public class LabApplication extends Application {

	public final Genre male = lookupOrCreate("male", id -> new Male(this, id));
	public final Genre female = lookupOrCreate("male", id -> new Female(this, id));
	public final Genre notgenred = lookupOrCreate("male", id -> new NotGenred(this, id));

	public final Status dr = lookupOrCreate("DR", id -> new DR(this, id));
	public final Status ir = lookupOrCreate("IR", id -> new IR(this, id));
	public final Status igr = lookupOrCreate("IGR", id -> new IGR(this, id));
	public final Status mcf = lookupOrCreate("MCF", id -> new MCF(this, id));
	public final Status prof = lookupOrCreate("Prof", id -> new Prof(this, id));

	public final Lab i3s = lookupOrCreate("i3s", id -> new I3S(this, id));

	public LabApplication(Element parent, ID id) {
		super(parent, id);
	}

	@Override
	public Class businessClass() {
		return LabNode.class;
	}
}
