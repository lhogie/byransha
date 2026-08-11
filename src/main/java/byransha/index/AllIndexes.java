package byransha.index;

import java.util.Objects;

import byransha.Element;
import byransha.action.base.ShowInKishanView;
import byransha.service.system.Hub;

public class AllIndexes extends Index {
	public final ByID byId;
	public final ByClass byClass;
	public final NodeList nodesList;

	public AllIndexes(Hub g) {
		super(g);
		byId = new ByID(this);
		byClass = new ByClass(g);
		nodesList = new NodeList(this);
	}

	@ShowInKishanView
	public long numberOfNodes() {
		return nodesList.size();
	}

	@Override
	public void add(Element n) {
		Objects.requireNonNull(n);
		byId.add(n);
		nodesList.add(n);
		byClass.add(n);
	}

	@Override
	public void delete(Element n) {
		nodesList.delete(n);
		byClass.delete(n);

		if (n.id() != null) {
			byId.delete(n);
		}
	}

	@Override
	public String strategy() {
		return "mixed";
	}

}