package byransha.nodes.primitive;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.view.NodeView;
import byransha.swing.ByranshaUserPane;

public class ListNodeView<T extends BNode> extends NodeView<ListNode<T>> {

	private JLabel label;

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
		n.get().forEach(e -> r.add(e.toJSONNode()));
		return r;
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		this.label = new JLabel();
		pane.append(label);
		pane.newLine();
		var jlist = new JList();
		jlist.setListData(n.get().toArray());
		jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlist.setCellRenderer(new ListCellRenderer<BNode>() {

			@Override
			public Component getListCellRendererComponent(JList<? extends BNode> list, BNode value, int index,
					boolean isSelected, boolean cellHasFocus) {
				JPanel p = new JPanel();
				var b = new JButton(value.prettyName());
				p.add(b);
				b.setOpaque(true);
				b.setBackground(isSelected ? Color.blue : Color.white);
//				p.add(value.findView(JumpTo.class);
				return p;
			}
		});

		var selectionModel = jlist.getSelectionModel();
		selectionModel.addListSelectionListener(e -> {
			n.selected = (List) Arrays.stream(selectionModel.getSelectedIndices())
					.mapToObj(i -> jlist.getModel().getElementAt(i)).toList();
			updateLabel();
		});

		var sp = new JScrollPane(jlist);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setPreferredSize(new Dimension(500, 100));
		pane.append(sp);
		updateLabel();
	}

	private void updateLabel() {
		label.setText(n.selected.size() + " selected, among " + n.get().size());
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

}