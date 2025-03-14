package byransha.labmodel.model.v0;

import byransha.BNode;
import byransha.BooleanNode;
import byransha.BBGraph;
import byransha.EmailNode;
import byransha.ImageNode;
import byransha.ListNode;
import byransha.StringNode;

public class Person extends BNode {
	public Person(BBGraph g) {
		super(g);
		// TODO Auto-generated constructor stub
	}

	public EtatCivil etatCivil = new EtatCivil(graph);
	public ListNode<Position> positions = new ListNode<>(graph);
	public ImageNode pics = new ImageNode(graph);
	public BooleanNode hdr = new BooleanNode(graph);

	public StringNode badgeNumber = new StringNode(graph);
	public StringNode website = new StringNode(graph);
	public StringNode faxNumber = new StringNode(graph);
	public ResearchGroup researchGroup;
	public StringNode phdDate = new StringNode(graph);

	public ListNode<StringNode> phoneNumbers = new ListNode<>(graph);
	protected ListNode<EmailNode> emailAddresses  = new ListNode<>(graph);
	public ListNode<Office> offices = new ListNode<>(graph);
	protected ListNode<ACMClassifier> topics;
	public StringNode quotite;

	public Position position;
	public boolean enposte;
	public StringNode researchActivity;

	@Override
	public String toString() {
		return etatCivil.name.get();
	}
}
