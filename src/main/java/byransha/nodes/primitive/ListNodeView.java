package byransha.nodes.primitive;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.action.list.ListNode;
import byransha.graph.view.NodeView;
import byransha.ui.swing.TranslatableTextArea;
import byransha.ui.swing.ChatSheet;
import byransha.ui.swing.Utils;
import byransha.util.ListChangeListener;

public class ListNodeView<T extends BNode> extends NodeView<ListNode<T>> {

	private TranslatableTextArea label;

	public ListNodeView(BGraph g, ListNode<T> l) {
		super(g, l);
	}

	@Override
	public String whatItShows() {
		return "all elements in a list";
	}

	@Override
	public JsonNode toJSON() {
		var r = new ArrayNode(null);
		viewedNode.elements.forEach(e -> r.add(e.toJSONNode()));
		return r;
	}

	@Override
	public void writeTo(ChatSheet pane) {
		this.label = new TranslatableTextArea(this);
		pane.appendToCurrentFlow(label);
		pane.newLine();
		var jlist = new JList();
		jlist.setListData(viewedNode.elements.toArray());
		jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlist.setCellRenderer(new ListCellRenderer<BNode>() {

			@Override
			public Component getListCellRendererComponent(JList<? extends BNode> list, BNode value, int index,
					boolean isSelected, boolean cellHasFocus) {
				var p = new JPanel(new FlowLayout(FlowLayout.LEFT));
				var b = new JLabel(value.prettyName());
				if (isSelected)
					b.setBackground(list.getSelectionBackground());
				p.add(Utils.idShower(value, 16, 4));
				p.add(b);
				return p;
			}
		});

		jlist.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = jlist.locationToIndex(e.getPoint());

					if (index >= 0) {
						pane.chat.append((BNode) jlist.getModel().getElementAt(index));
					}
				}
			}
		});

		var selectionModel = jlist.getSelectionModel();
		selectionModel.addListSelectionListener(e -> {
			Arrays.stream(selectionModel.getSelectedIndices()).forEach(i -> viewedNode.select(i));
			updateLabel();
		});

		pane.appendToCurrentFlow(Utils.resizableScrollPane(jlist));
		updateLabel();

		viewedNode.elements.listeners.add(new ListChangeListener<T>() {

			@Override
			public void onAdd(T element) {
				jlist.setListData(viewedNode.elements.toArray());
			}

			@Override
			public void onRemove(T element) {
				jlist.setListData(viewedNode.elements.toArray());
			}
		});
	}

	private void updateLabel() {
		label.setText(viewedNode.getSelected().size() + " selected, among " + viewedNode.elements.size());
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

}