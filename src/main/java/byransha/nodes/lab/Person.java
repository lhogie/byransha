package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.BusinessNode;
import byransha.graph.DocumentNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.Factory;
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
	public ListNode<Position> positions = new ListNode<Position>(parent, "positions", Position.class);
	@ShowInKishanView
	public DocumentNode pics = new DocumentNode(this);
	@ShowInKishanView
	public BooleanNode hdr = new BooleanNode(this, null);
	@ShowInKishanView
	public StringNode badgeNumber = new StringNode(this);
	@ShowInKishanView
	public StringNode website = new URLNode(this, null);
	@ShowInKishanView
	public final ListNode<Structure> structures = new ListNode<>(parent, "structures", Structure.class);
	@ShowInKishanView
	public DateNode phdDate = new DateNode(this);
	@ShowInKishanView
	public ListNode<PhoneNumberNode> phoneNumbers = new ListNode<PhoneNumberNode>(parent, "phone number(s)",
			PhoneNumberNode.class);
	@ShowInKishanView
	public ListNode<EmailNode> emailAddresses = new ListNode<EmailNode>(parent, "email adresses", EmailNode.class);
	@ShowInKishanView
	public ListNode<Office> offices = new ListNode<Office>(parent, "offices", Office.class);
	@ShowInKishanView
	public LongNode quotite = new LongNode(parent);
	public boolean enposte;
	@ShowInKishanView
	public ListNode<Publication> publications = new ListNode<>(parent, "publications", Publication.class);
	@ShowInKishanView
	public final StringNode orcid = new StringNode(parent, null, "^(\\d{4}-){3}\\d{3}(\\d|X)$");
	@ShowInKishanView
	public final StringNode authID = new StringNode(parent, null, "^A\\d{7}$");
	@ShowInKishanView
	public StringNode researchActivity;
	@ShowInKishanView
	public StringNode name = new StringNode(parent, null, ".+");
	@ShowInKishanView
	public StringNode firstName = new StringNode(parent, null, ".+");
	@ShowInKishanView
	public StringNode familyNameBeforeMariage = new StringNode(parent);
	@ShowInKishanView
	public StringNode cityOfBirth = new StringNode(parent, null, ".+");
	@ShowInKishanView
	public StringNode address = new StringNode(parent, null, ".+");
	public Country countryOfBirth;

	public ListNode<Nationality> nationality = new ListNode<Nationality>(parent, "nationalities", Nationality.class);
	public DateNode birthDate = new DateNode(this);
	public PhoneNumberNode telephone = new PhoneNumberNode(this);

	@Factory
	public Person(BNode parent) {
		super(parent);
		quotite.setBounds(new Bounds(0, 100));
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
