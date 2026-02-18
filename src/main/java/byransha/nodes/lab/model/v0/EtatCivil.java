package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.graph.DocumentNode;
import byransha.nodes.primitive.DateNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.PhoneNumberNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class EtatCivil extends BusinessNode {

	public StringNode name, firstName;

	public StringNode familyNameBeforeMariage, cityOfBirth, address;

	public ListNode<Country> countryOfBirth;

	public ListNode<Nationality> nationality;

	public DateNode birthDate;

	public PhoneNumberNode telephone;

	public DocumentNode pic;

	public EtatCivil(BBGraph g) {
		super(g);
		name = new StringNode(g);
		familyNameBeforeMariage = new StringNode(g);
		firstName = new StringNode(g);
		birthDate = new DateNode(g);
		cityOfBirth = new StringNode(g);
		countryOfBirth = new ListNode<Country>(g);
		nationality = new ListNode<Nationality>(g);
		address = new StringNode(g);
		telephone = new PhoneNumberNode(g);
	}

	@Override
	public String whatIsThis() {
		return "Etat Civil";
	}

	@Override
	public String prettyName() {
		String prettyName = "";
		if (name != null && name.get() != null && !name.get().isBlank()) {
			prettyName = name.get();
		}
		if (firstName != null && firstName.get() != null && !firstName.get().isBlank()) {
			prettyName += " " + firstName.get();
		}
		if (prettyName.isBlank()) {
			prettyName = null;
		}
		return prettyName;
	}
}
