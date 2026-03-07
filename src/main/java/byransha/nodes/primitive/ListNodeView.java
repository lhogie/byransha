package byransha.nodes.primitive;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.view.NodeView;
import byransha.ui.swing.ByranshaUserPane;
import byransha.ui.swing.ResizableByGrip;
import byransha.ui.swing.Utils;

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
		viewedNode.get().forEach(e -> r.add(e.toJSONNode()));
		return r;
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		this.label = new JLabel();
		pane.appendToCurrentFlow(label);
		pane.newLine();
		var jlist = new JList();
		jlist.setListData(viewedNode.get().toArray());
		jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlist.setCellRenderer(new ListCellRenderer<BNode>() {

			@Override
			public Component getListCellRendererComponent(JList<? extends BNode> list, BNode value, int index,
					boolean isSelected, boolean cellHasFocus) {
				var b = value.createJumpComponent();
				if (isSelected)
					b.setBackground(list.getSelectionBackground());
				b.setText(value.whatIsThis() + ": " + b.getText());
				return b;
			}
		});

		jlist.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = jlist.locationToIndex(e.getPoint());

					if (index >= 0) {
						currentUser().jumpTo((BNode) jlist.getModel().getElementAt(index));
					}
				}
			}
		});
		var selectionModel = jlist.getSelectionModel();
		selectionModel.addListSelectionListener(e -> {
			viewedNode.setSelected((List) Arrays.stream(selectionModel.getSelectedIndices())
					.mapToObj(i -> jlist.getModel().getElementAt(i)).toList());
			updateLabel();
		});

		
		pane.appendToCurrentFlow(Utils.resizableScrollPane(jlist));
		updateLabel();
	}

	private void updateLabel() {
		label.setText(viewedNode.getSelected().size() + " selected, among " + viewedNode.get().size());
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

}