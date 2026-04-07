package byransha.nodes.primitive;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.action.list.ListNode;
import byransha.graph.view.NodeView;
import byransha.ui.swing.ChatSheet;
import byransha.ui.swing.ErrorIndicator;
import byransha.ui.swing.TextDisplayComponent;
import byransha.ui.swing.Utils;
import byransha.util.ListenableList;

public class ListNodeView<T extends BNode> extends NodeView<ListNode<T>> {
	private StringNode label = new StringNode(g);
	private BooleanNode oneElementPerLine = new BooleanNode(g, false);
	private Map<BNode, JCheckBox> selectionsBoxes = new HashMap<>();

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
	public void writeTo(ChatSheet sheet) {
		updateLabel();
		sheet.currentLine.add(new TextDisplayComponent(g.translator, label.get()));
		var line = sheet.newLine();

		for (int i = 0; i < viewedNode.elements.size(); ++i) {
			var element = viewedNode.elements.get(i);
			var elementPanel = listElement(element, i + 1, sheet);
			line.add(elementPanel);

			if (oneElementPerLine.get()) {
				sheet.newLine();
			}
		}

		if (!oneElementPerLine.get()) {
			sheet.newLine();
		}

		viewedNode.elements.addListener(new ListenableList.Listener<T>() {

			@Override
			public void onAdded(int index, T element) {
				updateLabel();
				line.add(listElement(element, index, sheet), index);
			}

			@Override
			public void onRemoved(int index, T oldElement) {
				updateLabel();
				line.remove(index);
			}

			@Override
			public void onSet(int index, T oldElement, T newElement) {
			}

		});

		viewedNode.selection.addListener(new ListenableList.Listener<T>() {

			@Override
			public void onAdded(int index, T element) {
				updateLabel();
				selectionsBoxes.get(element).setSelected(true);
			}

			@Override
			public void onRemoved(int index, T element) {
				updateLabel();
				selectionsBoxes.get(element).setSelected(true);
			}

			@Override
			public void onSet(int index, T oldElement, T newElement) {
				selectionsBoxes.get(oldElement).setSelected(false);
				selectionsBoxes.get(newElement).setSelected(true);
			}

		});
	}

	private JComponent listElement(T element, int i, ChatSheet chat) {
		var sheet = new ChatSheet(chat.chat);
		sheet.setToolTipText(element + ", " + element.whatIsThis());
		sheet.setOpaque(false);
		sheet.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		var selectionBox = new JCheckBox();
		selectionsBoxes.put(element, selectionBox);
		selectionBox.setSize(new Dimension(10, 10));
		selectionBox.setSelected(viewedNode.getSelected().contains(element));
		selectionBox.addActionListener(e -> {
			if (selectionBox.isSelected()) {
				viewedNode.getSelected().add(element);
			} else {
				viewedNode.getSelected().remove(element);
			}
			updateLabel();
		});
		sheet.appendToCurrentLine(selectionBox);

		sheet.appendToCurrentLine(new JLabel(i + ""));
		sheet.appendToCurrentLine(Utils.idShower(element, 16, 4, chat.chat));
		sheet.appendToCurrentLine(new ErrorIndicator(element));

		if (element instanceof PrimitiveValueNode) {
			element.views().getFirst().writeTo(sheet);
		} else {
			sheet.appendToCurrentLine(element.toString(), element.g.translator);
		}

		var removeButton = new JButton(Utils.icon("close_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24", 0.5));
		removeButton.setToolTipText("remove this element from the list");
		removeButton.addActionListener(e -> viewedNode.elements.remove(element));
		sheet.appendToCurrentLine(removeButton);

		return sheet;
	}

	private void updateLabel() {
		if (viewedNode.elements.size() == 0) {
			label.set("empty list");
			return;
		}

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