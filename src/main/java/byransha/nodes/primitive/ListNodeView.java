package byransha.nodes.primitive;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.action.list.ListNode;
import byransha.graph.view.NodeView;
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

		for (int i = 0; i < viewedNode.elements.size(); ++i) {
			var element = viewedNode.elements.get(i);
			var elementPanel = elementPanel(element, i);
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
				int i = viewedNode.get().indexOf(element);
				line.add(elementPanel(element, i), i);
				updateLabel();
			}

			@Override
			public void onRemove(T element) {
				int i = viewedNode.get().indexOf(element);
				line.remove(i);
				updateLabel();
			}
		});
	}

	private JComponent elementPanel(T element, int i) {
		var elementPanel = new JPanel();
		elementPanel.setBorder(new LineBorder(Color.black));
		elementPanel.add(new JLabel(i + ""));
		elementPanel.add(Utils.idShower(element, 16, 4));
		elementPanel.add(new ErrorIndicator(element));
		var sheet = new ChatSheet(null);
		element.views().getFirst().writeTo(sheet);
		elementPanel.add(sheet);
		return elementPanel;
	}

	private void updateLabel() {
		label.set(viewedNode.getSelected().size() + " selected, among " + viewedNode.elements.size());
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

}