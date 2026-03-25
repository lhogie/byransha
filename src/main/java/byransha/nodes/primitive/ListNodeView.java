package byransha.nodes.primitive;

import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.action.list.ListNode;
import byransha.graph.view.NodeView;
import byransha.nodes.system.ChatNode;
import byransha.ui.swing.ChatSheet;
import byransha.ui.swing.ErrorIndicator;
import byransha.ui.swing.Sheet;
import byransha.ui.swing.TextDisplayComponent;
import byransha.ui.swing.Utils;
import byransha.util.ListChangeListener;

public class ListNodeView<T extends BNode> extends NodeView<ListNode<T>> {
	private StringNode label = new StringNode(g);
	private BooleanNode oneElementPerLine = new BooleanNode(g, false);

	public ListNodeView(BGraph g, ListNode<T> l) {
		super(g, l);
	}

	@Override
	public String whatItShows() {
		return "all elements in a list";
	}

	@Override
	public ArrayNode jsonView() {
		var a = new ArrayNode(null);
		viewedNode.elements.forEach(e -> a.add(e.id()));
		return a;
	}

	@Override
	public void writeTo(Sheet sheet) {
		updateLabel();
		sheet.currentLine.add(new TextDisplayComponent(g.translator, label.get()));
		var line = sheet.newLine();
var chat = ((ChatSheet) sheet).chat; 
		for (int i = 0; i < viewedNode.elements.size(); ++i) {
			var element = viewedNode.elements.get(i);
			var elementPanel = elementPanel(element, i + 1, chat);
			line.add(elementPanel);

			if (oneElementPerLine.get()) {
				sheet.newLine();
			}
		}

		if (!oneElementPerLine.get()) {
			sheet.newLine();
		}

		viewedNode.elements.listeners.add(new ListChangeListener<T>() {

			@Override
			public void onAdd(T element) {
				updateLabel();
				int i = viewedNode.get().indexOf(element);
				line.add(elementPanel(element, i, chat), i);
			}

			@Override
			public void onRemove(T element) {
				updateLabel();
				int i = viewedNode.get().indexOf(element);
				line.remove(i);
			}
		});
	}

	private JComponent elementPanel(T element, int i, ChatNode chat) {
		var elementPanel = new JPanel();
		elementPanel.setToolTipText(element.prettyName() + ", " + element.whatIsThis());
		elementPanel.setOpaque(false);
		elementPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		var sheet = new Sheet();
		sheet.appendToCurrentLine(new JLabel(i + ""));
		sheet.appendToCurrentLine(Utils.idShower(element, 16, 4, chat));
		sheet.appendToCurrentLine(new ErrorIndicator(element));

		if (element instanceof PrimitiveValueNode) {
			element.views().getFirst().writeTo(sheet);
		} else {
			var s = element.prettyName();
			sheet.appendToCurrentLine(s, element.g.translator);
		}

		elementPanel.add(sheet);
		return elementPanel;
	}

	private void updateLabel() {
		var s = viewedNode.getSelected().size() + " selected, among " + viewedNode.elements.size();

		if (viewedNode.elements.size() > 0) {
			s += " (";
			var map = viewedNode.elements.stream().collect(Collectors.groupingBy(Object::getClass));
			for (var e : map.entrySet()) {
				s += e.getValue().size() + " " + e.getValue().getFirst().whatIsThis() + "(s) ";
			}
			s += ")";
		}

		label.set(s);
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

}