package byransha.labmodel.model.v0;

import byransha.*;

public class Person extends BusinessNode {
	public EtatCivil etatCivil;
	public SetNode<Position> positions;
	public ImageNode pics;
	public BooleanNode hdr;

	public StringNode badgeNumber;
	public StringNode website;
	public StringNode faxNumber;
	public ResearchGroup researchGroup;
	public DateNode phdDate;

	public SetNode<StringNode> phoneNumbers;
	protected SetNode<EmailNode> emailAddresses;
	public SetNode<Office> offices;
	protected SetNode<ACMClassifier> topics;
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
		if( etatCivil == null || etatCivil.name == null || etatCivil.name.get() == null || etatCivil.name.get().isEmpty()) {
			return this.id() + "@Unnamed Person";
		}
		return etatCivil.name.get();
	}

	public Person(BBGraph g) {
		super(g);

		etatCivil = BNode.create(g, EtatCivil.class); // new EtatCivil(g);
		positions = BNode.create(g, SetNode.class); // new ListNode<>(g);
		pics = BNode.create(g, ImageNode.class); // new ImageNode(g);

		hdr = BNode.create(g, BooleanNode.class); // new BooleanNode(g);

		badgeNumber = BNode.create(g, StringNode.class); // new StringNode(g, null);
		website = BNode.create(g, StringNode.class); // new StringNode(g, null);
		faxNumber = BNode.create(g, StringNode.class); // new StringNode(g, null);
		phdDate = BNode.create(g, DateNode.class); // new StringNode(g, null);

		phoneNumbers = BNode.create(g, SetNode.class); // new ListNode<>(g);
		emailAddresses = BNode.create(g, SetNode.class); // new ListNode<>(g);
		offices = BNode.create(g, SetNode.class); // new ListNode<>(g);
	}

	public Person(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "a physical person working in the lab";
	}
}
