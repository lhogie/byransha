package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.graph.DocumentNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.ByBoolean;
import byransha.nodes.primitive.DateNode;
import byransha.nodes.primitive.EmailNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Person extends BusinessNode {

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

	protected ListNode<ACMClassifier> topics;

	public StringNode quotite;

	public Position position;
	public boolean enposte;
	public StringNode researchActivity;

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

	public Person(BBGraph g, User creator) {
		super(g, creator);
		etatCivil = new EtatCivil(g, creator);
		positions = new ListNode(g, creator);
		pics = new DocumentNode(g, creator);
		hdr = new BooleanNode(g, creator, ByBoolean.DUNNO);
		badgeNumber = new StringNode(g, creator);
		website = new StringNode(g, creator);
		faxNumber = new StringNode(g, creator);
		phdDate = new DateNode(g, creator);
		phoneNumbers = new ListNode(g, creator);
		emailAddresses = new ListNode(g, creator);
		offices = new ListNode(g, creator);
	}

	@Override
	public String whatIsThis() {
		return "a physical person working in the lab";
	}
}
