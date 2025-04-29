package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BooleanNode;
import byransha.EmailNode;
import byransha.ImageNode;
import byransha.ListNode;
import byransha.StringNode;

public class Person extends BusinessNode {
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

		etatCivil = g.addNode(EtatCivil.class); //new EtatCivil(g);
		positions = g.addNode(ListNode.class); //new ListNode<>(g);
		pics =  g.addNode(ImageNode.class); //new ImageNode(g);

		hdr = g.addNode(BooleanNode.class); //new BooleanNode(g);

		badgeNumber = g.addNode(StringNode.class); //new StringNode(g, null);
		website = g.addNode(StringNode.class); //new StringNode(g, null);
		faxNumber = g.addNode(StringNode.class); //new StringNode(g, null);
		phdDate = g.addNode(StringNode.class); //new StringNode(g, null);

		phoneNumbers = g.addNode(ListNode.class); //new ListNode<>(g);
		emailAddresses = g.addNode(ListNode.class); //new ListNode<>(g);
		offices = g.addNode(ListNode.class); //new ListNode<>(g);
	}

	public Person(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "a physical person working in the lab";
	}
}
