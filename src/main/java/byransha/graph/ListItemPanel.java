package byransha.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import byransha.graph.list.action.ListNode;
import byransha.nodes.system.ChatNode;
import byransha.ui.swing.ErrorIndicator;
import byransha.ui.swing.Utils;

public class ListItemPanel extends JPanel {
	public final JCheckBox selectionBox = new JCheckBox();
//	public final JLabel label;

	public ListItemPanel(BNode node, ListNode list, int i, ChatNode chat) {
		super(new FlowLayout());
		setOpaque(false);
		setBorder(new LineBorder(Color.black, 1));
		selectionBox.setSize(new Dimension(10, 10));
//		add(selectionBox);
		setToolTipText(this + ", " + node.whatIsThis());
		setBackground(Color.yellow);
		selectionBox.setSelected(list.getSelected().contains(this));
		selectionBox.addActionListener(e -> {
			if (selectionBox.isSelected()) {
				list.getSelected().add(node);
			} else {
				list.getSelected().remove(node);
			}
		});

//		add(label = new JLabel(i + ""));
		add(node.createBall(16, 4, chat));
		add(new ErrorIndicator(node));
		add(node.getListItemComponent(chat));

		var removeButton = new JButton(Utils.icon("close_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24", 0.5));
		removeButton.setToolTipText("remove this element from the list");
		removeButton.addActionListener(e -> list.elements.remove(node));
		add(removeButton);

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (list.selection.contains(node)) {
					list.selection.remove(node);
					setBorder(new LineBorder(Color.black, 1));
				} else {
					list.selection.add(node);
					setBorder(new LineBorder(Color.black, 3));
				}
			}
		});
	}
}