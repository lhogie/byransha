package byransha.graph.action.table;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.view.NodeView;
import byransha.ui.swing.ChatSheet;
import byransha.ui.swing.Utils;
import javafx.scene.control.TableSelectionModel;

public class TableNodeView extends NodeView<TableNode> {
	private JLabel label;

	public TableNodeView(BGraph g, TableNode node) {
		super(g, node);
	}

	@Override
	public void writeTo(ChatSheet pane) {
		this.label = new JLabel();
		pane.appendToCurrentFlow(label);
		pane.newLine();
		var jtable = new JTable();
//		jtable.setListData(viewedNode.values.toArray());
		jtable.setSelectionMode(Ta);
		jtable.setDefaultRendererer(Class clazz, new TableCellRenderer() {
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				var b = value.createJumpComponent(pane.chat);
				if (isSelected)
					b.setBackground(list.getSelectionBackground());
				b.setText(value.whatIsThis() + ": " + b.getText());
				return b;
			}
		};
		

		var selectionModel = jtable.getSelectionModel();
		selectionModel.addListSelectionListener(e -> {
			Arrays.stream(selectionModel.getSelectedIndices()).forEach(i -> viewedNode.select(i));
			updateLabel();
		});

		pane.appendToCurrentFlow(Utils.resizableScrollPane(jtable));
		updateLabel();
	}

	private void updateLabel() {
		label.setText(viewedNode.getSelected().size() + " selected, among " + viewedNode.values.size());
	}
	@Override
	public JsonNode toJSON() {
		return null;
	}

	@Override
	public String whatItShows() {
		return viewedNode.label;
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

}
