package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.DocumentNode;
import byransha.graph.action.list.ListNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.DateNode;
import byransha.nodes.primitive.EmailNode;
import byransha.nodes.primitive.StringNode;

public class Person extends BusinessNode {

	public Genre genre;
	public EtatCivil etatCivil;
	public ListNode<Position> positions;
	public DocumentNode pics;
	public BooleanNode hdr;
	public StringNode badgeNumber;
	public StringNode website;
	public StringNode faxNumber;
	public ResearchGroup researchGroup;
	public DateNode phdDate;
	public ListNode<StringNode> phoneNumbers;
	protected ListNode<EmailNode> emailAddresses;
	public ListNode<Office> offices;
	public StringNode quotite;
	public Position position;
	public boolean enposte;
	public StringNode researchActivity;
	public ListNode<Publication> publications;
	public final StringNode orcid;

	public Person(BGraph g) {
		super(g);
		orcid = new StringNode(g);
		etatCivil = new EtatCivil(g);
		positions = new ListNode(g, "positions");
		pics = new DocumentNode(g);
		hdr = new BooleanNode(g, null);
		badgeNumber = new StringNode(g);
		website = new StringNode(g);
		faxNumber = new StringNode(g);
		phdDate = new DateNode(g);
		phoneNumbers = new ListNode(g, "phone number(s)");
		emailAddresses = new ListNode(g, "email adresses");
		offices = new ListNode(g, "offices");
	}

	@Override
	public String toString() {
		if (etatCivil == null) {
			return super.toString();
		}
		return etatCivil.name.get();
	}

	@Override
	public String prettyName() {
		if (etatCivil == null) {
			return null;
		}

		return etatCivil.prettyName();
	}

	@Override
	public String whatIsThis() {
		return "a physical person working in the lab";
	}
}
