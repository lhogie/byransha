package byransha.action.base;

import java.util.Arrays;

import byransha.Element;
import byransha.ID;
import byransha.action.Action;
import byransha.action.base.FreezingAction.misc;
import byransha.list.action.ListNode;
import byransha.primitive.TextNode;

public class JumpToAnotherNode extends Action {
	final TextNode text = new TextNode(this, null, "list of IDs", "");
	ListNode<Element> nodes = new ListNode<>(this, null, "nodes", Element.class);

	public JumpToAnotherNode(Element g) {
		super(g, misc.class);
		text.addValueChangeListener((a, b, c) -> {
			nodes.elements.clear();
			Arrays.stream(text.get().replace(',', '\n').split("\n")).forEach(s -> {
				try {
					nodes.elements.add(hub().indexes.byId.get(ID.fromBase62(s.trim())));
				} catch (Throwable err) {
					hub().errorLog.add(err);
				}
			});
		});
	}

	@Override
	public String whatItDoes() {
		return "convert IDs to nodes";
	}

	@Override
	public void impl() {
	}

	@Override
	public boolean applies() {
		return true;
	}
}
