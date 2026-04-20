package byransha.graph;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import byransha.graph.list.action.ListNode;
import byransha.nodes.system.ChatNode;
import byransha.ui.swing.ErrorIndicator;

public class ListItemPanel extends JPanel {
	public final JCheckBox selectionBox = new JCheckBox();
	public final javax.swing.JLabel label;

	public ListItemPanel(BNode node, ListNode list, int i, ChatNode chat) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setOpaque(false);
		setBorder(new LineBorder(Color.lightGray, 1));
		setToolTipText(this + ", " + node.whatIsThis());

		add(label = new javax.swing.JLabel(i + ""));
		add(node.createBall(16, 4, chat));
		add(new ErrorIndicator(node));
		var c = node.getListItemComponent(chat);
		add(c);

		
		var ml = new MouseListener() {

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
					setBorder(new LineBorder(Color.lightGray, 1));
				} else {
					list.selection.add(node);
					setBorder(new LineBorder(chat.g().swing.backgroundColor.get(), 1));
				}
			}
		};
		
		addMouseListener(ml);
		c.addMouseListener(ml);
	}
}