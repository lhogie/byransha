package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.DocumentNode;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.DateNode;
import byransha.nodes.primitive.PhoneNumberNode;
import byransha.nodes.primitive.StringNode;

public class EtatCivil extends BusinessNode {

	public StringNode name, firstName;

	public StringNode familyNameBeforeMariage, cityOfBirth, address;

	public ListNode<Country> countryOfBirth;

	public ListNode<Nationality> nationality;

	public DateNode birthDate;

	public PhoneNumberNode telephone;

	public DocumentNode pic;

	public EtatCivil(BGraph g) {
		super(g);
		name = new StringNode(g);
		familyNameBeforeMariage = new StringNode(g);
		firstName = new StringNode(g);
		birthDate = new DateNode(g);
		cityOfBirth = new StringNode(g);
		countryOfBirth = new ListNode<Country>(g, "countries");
		nationality = new ListNode<Nationality>(g, "nationalities");
		address = new StringNode(g);
		telephone = new PhoneNumberNode(g);
	}

	@Override
	public String whatIsThis() {
		return "Etat Civil";
	}

	@Override
	public String toString() {
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
