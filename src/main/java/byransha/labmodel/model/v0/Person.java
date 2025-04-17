package byransha.labmodel.model.v0;

import byransha.BNode;
import byransha.BooleanNode;
import byransha.BBGraph;
import byransha.EmailNode;
import byransha.ImageNode;
import byransha.ListNode;
import byransha.StringNode;

public class Person extends BNode {
	public EtatCivil etatCivil;
	public ListNode<Position> positions;
	public ImageNode pics;
	public BooleanNode hdr;

	public StringNode badgeNumber;
	public StringNode website;
	public StringNode faxNumber;
	public ResearchGroup researchGroup;
	public StringNode phdDate;

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
			return "null";
		}
		return etatCivil.name.get();
	}
	
	@Override
	public String prettyName() {
		return etatCivil.name.get();
	}
	
	
	public Person(BBGraph g) {
		super(g);

		etatCivil = new EtatCivil(g);
		positions = new ListNode<>(g);
		pics = new ImageNode(g);
		hdr = new BooleanNode(g);

		badgeNumber = new StringNode(g, null);
		website = new StringNode(g, null);
		faxNumber = new StringNode(g, null);
		phdDate = new StringNode(g, null);

		phoneNumbers = new ListNode<>(g);
		emailAddresses  = new ListNode<>(g);
		offices = new ListNode<>(g);
	}

	public Person(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "a physical person working in the lab";
	}
}
