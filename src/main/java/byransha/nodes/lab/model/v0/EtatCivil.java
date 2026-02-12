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

	public EtatCivil(BBGraph g, User creator) {
		super(g, creator);
		name = new StringNode(g, creator);
		familyNameBeforeMariage = new StringNode(g, creator);
		firstName = new StringNode(g, creator);
		birthDate = new DateNode(g, creator);
		cityOfBirth = new StringNode(g, creator);
		countryOfBirth = new ListNode<Country>(g, creator);
		nationality = new ListNode<Nationality>(g, creator);
		address = new StringNode(g, creator);
		telephone = new PhoneNumberNode(g, creator);
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
