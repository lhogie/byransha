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

		etatCivil = (EtatCivil) g.addNode(EtatCivil.class); //new EtatCivil(g);
		positions = (ListNode) g.addNode(ListNode.class); //new ListNode<>(g);
		pics = (ImageNode) g.addNode(ImageNode.class); //new ImageNode(g);

		hdr = (BooleanNode) g.addNode(BooleanNode.class); //new BooleanNode(g);

		badgeNumber = (StringNode) g.addNode(StringNode.class); //new StringNode(g, null);
		website = (StringNode) g.addNode(StringNode.class); //new StringNode(g, null);
		faxNumber = (StringNode) g.addNode(StringNode.class); //new StringNode(g, null);
		phdDate = (StringNode) g.addNode(StringNode.class); //new StringNode(g, null);

		phoneNumbers = (ListNode) g.addNode(ListNode.class); //new ListNode<>(g);
		emailAddresses = (ListNode) g.addNode(ListNode.class); //new ListNode<>(g);
		offices = (ListNode) g.addNode(ListNode.class); //new ListNode<>(g);
	}

	public Person(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "a physical person working in the lab";
	}
}
