package byransha.lab;

import javax.swing.JComponent;
import javax.swing.JLabel;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.graph.relection.Factory;
import byransha.primitive.BooleanNode;
import byransha.primitive.DateNode;
import byransha.primitive.EmailNode;
import byransha.primitive.LongNode;
import byransha.primitive.LongNode.Bounds;
import byransha.primitive.PhoneNumberNode;
import byransha.primitive.StringNode;
import byransha.primitive.URLNode;
import byransha.system.ChatNode;

public class Person extends LabNode {
	@ShowInKishanView
	public Genre genre;
	@ShowInKishanView
	public ListNode<Position> positions = lookupOrCreate("positions",
			id -> new ListNode<Position>(this, id, "positions", Position.class));
	@ShowInKishanView
	public ImageNode pics = lookupOrCreate("pic", id -> new ImageNode(this, id));
	@ShowInKishanView
	public final BooleanNode hdr = lookupOrCreate("hdr", id -> new BooleanNode(this, id, null));
	@ShowInKishanView
	public final BadgeNode badgeNumber = lookupOrCreate("name", id -> new BadgeNode(this, id));
	@ShowInKishanView
	public URLNode website = lookupOrCreate("website", id -> new URLNode(this, id, null));
	@ShowInKishanView
	public final ListNode<Structure> structures = lookupOrCreate("strcutures",
			id -> new ListNode<>(this, id, "structures", Structure.class));
	@ShowInKishanView
	public DateNode phdDate = lookupOrCreate("phdDate", id -> new DateNode(this, id));
	@ShowInKishanView
	public ListNode<PhoneNumberNode> phoneNumbers = lookupOrCreate("phoneNumbers",
			id -> new ListNode<PhoneNumberNode>(this, id, "phone number(s)", PhoneNumberNode.class));
	@ShowInKishanView

	public final ListNode<EmailNode> emailAddresses = lookupOrCreate("emailAddresses",
			id -> new ListNode<EmailNode>(this, id, "email adresses", EmailNode.class));

	@ShowInKishanView
	public ListNode<Room> offices = lookupOrCreate("offices", id -> new ListNode<Room>(this, id, "offices", Room.class));

	@ShowInKishanView
	public final LongNode quotite = lookupOrCreate("quotite", id -> new LongNode(this, id));
	public boolean enposte;
	@ShowInKishanView
	public ListNode<Publication> publications = lookupOrCreate("publications", id -> new ListNode<>(this, id, "publications", Publication.class));
	@ShowInKishanView
	public final StringNode orcid = lookupOrCreate("orcid", id -> new StringNode(this, id, null, "^(\\d{4}-){3}\\d{3}(\\d|X)$"));
	@ShowInKishanView
	public final StringNode authID = lookupOrCreate("autID", id -> new StringNode(this, id, null, "^A\\d{7}$"));
	@ShowInKishanView
	public StringNode researchActivity;
	@ShowInKishanView
	public StringNode name = lookupOrCreate("name", id -> new StringNode(this, id, null, ".+"));
	@ShowInKishanView
	public StringNode firstName = lookupOrCreate("firstName", id -> new StringNode(this, id, null, ".+"));
	@ShowInKishanView
	public StringNode familyNameBeforeMariage = lookupOrCreate("familyNameBeforeMariage", id -> new StringNode(this, id, null, null));
	@ShowInKishanView
	public StringNode cityOfBirth = lookupOrCreate("cityOfBirth", id -> new StringNode(this, id, null, ".+"));
	@ShowInKishanView
	public AddressNode address = lookupOrCreate("address", id -> new AddressNode(this, id));
	public Country countryOfBirth;

	public ListNode<Country> nationality = lookupOrCreate("nationality", id -> new ListNode<Country>(this, id, "nationalities", Country.class));
	public DateNode birthDate = lookupOrCreate("birthDate", id -> new DateNode(this, id));
	public PhoneNumberNode telephone = lookupOrCreate("phoneNumber", id -> new PhoneNumberNode(this, id));

	@Factory
	public Person(Element parent, ID id) {
		super(parent, id);
		quotite.setBounds(new Bounds(0, 100));
	}

	@Override
	public String toString() {
		return firstName.toString() + " " + name.toString();
	}

	@Override
	public String whatIsThis() {
		return "a physical person working in the lab";
	}

	@Override
	public JComponent getListItemComponent(ChatNode chat) {
		return new JLabel(firstName.get() + " " + name.get());
	}

}
