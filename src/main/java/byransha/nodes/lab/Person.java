package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.DocumentNode;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.DateNode;
import byransha.nodes.primitive.EmailNode;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.primitive.LongNode.Bounds;
import byransha.nodes.primitive.PhoneNumberNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.URLNode;

public class Person extends BusinessNode {

	public Genre genre;
	public ListNode<Position> positions;
	public DocumentNode pics;
	public BooleanNode hdr;
	public StringNode badgeNumber;
	public StringNode website;
	public StringNode faxNumber;
	public final ListNode<Structure> structures = new ListNode<>(g, "structures");
	public DateNode phdDate;
	public ListNode<PhoneNumberNode> phoneNumbers;
	protected ListNode<EmailNode> emailAddresses;
	public ListNode<Office> offices;
	public LongNode quotite = new LongNode(g);
	public Position position;
	public boolean enposte;
	public ListNode<Publication> publications = new ListNode<>(g, "publications");
	public final StringNode orcid = new StringNode(g, null, "^(\\d{4}-){3}\\d{3}(\\d|X)$");
	public final StringNode authID = new StringNode(g, null, "^A\\d{7}$");
	public StringNode researchActivity;
	public StringNode name = new StringNode(g, null, ".+");
	public StringNode firstName = new StringNode(g, null, ".+");

	public StringNode familyNameBeforeMariage = new StringNode(g);
	public StringNode cityOfBirth = new StringNode(g, null, ".+");
	public StringNode address = new StringNode(g, null, ".+");

	public ListNode<Country> countryOfBirth = new ListNode<Country>(g, "countries");;

	public ListNode<Nationality> nationality = new ListNode<Nationality>(g, "nationalities");;

	public DateNode birthDate = new DateNode(g);

	public PhoneNumberNode telephone = new PhoneNumberNode(g);;

	public DocumentNode pic;
	
	public Person(BGraph g) {
		super(g);
		quotite.setBounds(new Bounds(0, 100));
		
		
		
		positions = new ListNode<Position>(g, "positions");
		pics = new DocumentNode(g);
		hdr = new BooleanNode(g, null);
		badgeNumber = new StringNode(g);
		website = new URLNode(g, null);
		faxNumber = new StringNode(g);
		phdDate = new DateNode(g);
		phoneNumbers = new ListNode<PhoneNumberNode>(g, "phone number(s)");
		emailAddresses = new ListNode<EmailNode>(g, "email adresses");
		offices = new ListNode<Office>(g, "offices");
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

	@Override
	public String whatIsThis() {
		return "a physical person working in the lab";
	}
}
