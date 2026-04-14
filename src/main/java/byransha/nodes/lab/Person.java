package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.DocumentNode;
import byransha.graph.ShowInKishanView;
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
	@ShowInKishanView
	public Genre genre;
	@ShowInKishanView
	public ListNode<Position> positions = new ListNode<Position>(g, "positions", Position.class);
	@ShowInKishanView
	public DocumentNode pics;
	@ShowInKishanView
	public BooleanNode hdr;
	@ShowInKishanView
	public StringNode badgeNumber;
	@ShowInKishanView
	public StringNode website;
	@ShowInKishanView
	public final ListNode<Structure> structures = new ListNode<>(g, "structures", Structure.class);
	@ShowInKishanView
	public DateNode phdDate;
	@ShowInKishanView
	public ListNode<PhoneNumberNode> phoneNumbers = new ListNode<PhoneNumberNode>(g, "phone number(s)", PhoneNumberNode.class);
	@ShowInKishanView
	public ListNode<EmailNode> emailAddresses = new ListNode<EmailNode>(g, "email adresses", EmailNode.class);
	@ShowInKishanView
	public ListNode<Office> offices = new ListNode<Office>(g, "offices", Office.class);
	@ShowInKishanView
	public LongNode quotite = new LongNode(g);
	public boolean enposte;
	@ShowInKishanView
	public ListNode<Publication> publications = new ListNode<>(g, "publications", Publication.class);
	@ShowInKishanView
	public final StringNode orcid = new StringNode(g, null, "^(\\d{4}-){3}\\d{3}(\\d|X)$");
	@ShowInKishanView
	public final StringNode authID = new StringNode(g, null, "^A\\d{7}$");
	@ShowInKishanView
	public StringNode researchActivity;
	@ShowInKishanView
	public StringNode name = new StringNode(g, null, ".+");
	@ShowInKishanView
	public StringNode firstName = new StringNode(g, null, ".+");
	@ShowInKishanView
	public StringNode familyNameBeforeMariage = new StringNode(g);
	@ShowInKishanView
	public StringNode cityOfBirth = new StringNode(g, null, ".+");
	@ShowInKishanView
	public StringNode address = new StringNode(g, null, ".+");
	public Country countryOfBirth;

	public ListNode<Nationality> nationality = new ListNode<Nationality>(g, "nationalities", Nationality.class);
	public DateNode birthDate = new DateNode(g);
	public PhoneNumberNode telephone = new PhoneNumberNode(g);;

	public Person(BGraph g) {
		super(g);
		quotite.setBounds(new Bounds(0, 100));

		pics = new DocumentNode(g);
		hdr = new BooleanNode(g, null);
		badgeNumber = new StringNode(g);
		website = new URLNode(g, null);
		phdDate = new DateNode(g);
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
