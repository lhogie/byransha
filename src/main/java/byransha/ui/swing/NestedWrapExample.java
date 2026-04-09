package byransha.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

public class NestedWrapExample {

	// ── The real fix: implement Scrollable to constrain width
	// ─────────────────────
	public static class WrapPanel extends JPanel implements Scrollable {

		public WrapPanel() {
			setLayout(new WrapLayout());
		}

		// This is the KEY method — tells JScrollPane "don't give me infinite width"
		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true; // forces panel width = viewport width → wrapping works
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
			return 16;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
			return 16;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
	}

	// ── WrapLayout: FlowLayout that correctly computes wrapped height
	// ──────────────
	public static class WrapLayout extends FlowLayout {

		public WrapLayout() {
			super(FlowLayout.LEFT, 5, 5);
		}

		@Override
		public Dimension preferredLayoutSize(Container target) {
			synchronized (target.getTreeLock()) {
				int width = target.getWidth();
				if (width == 0)
					width = 800; // fallback before first paint

				Insets ins = target.getInsets();
				int avail = width - ins.left - ins.right;
				int rowW = 0;
				int rowH = 0;
				int totalH = ins.top + ins.bottom + getVgap();

				for (Component c : target.getComponents()) {
					if (!c.isVisible())
						continue;
					Dimension d = c.getPreferredSize();
					int needed = rowW == 0 ? d.width : rowW + getHgap() + d.width;
					if (rowW > 0 && needed > avail) {
						totalH += rowH + getVgap();
						rowW = 0;
						rowH = 0;
					}
					rowW += (rowW == 0 ? 0 : getHgap()) + d.width;
					rowH = Math.max(rowH, d.height);
				}
				totalH += rowH;
				return new Dimension(width, totalH);
			}
		}

		@Override
		public Dimension minimumLayoutSize(Container target) {
			return preferredLayoutSize(target);
		}
	}

	// ── Demo
	// ──────────────────────────────────────────────────────────────────────
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Nested WrapPanel");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(500, 400);

			// Outer wrap panel (directly in scroll pane → implements Scrollable)
			WrapPanel outerPanel = new WrapPanel();
			outerPanel.setBackground(new Color(230, 240, 255));

			for (int g = 0; g < 3; g++) {
				// Inner wrap panel (nested inside outer)
				WrapPanel innerPanel = new WrapPanel();
				innerPanel.setBackground(new Color(180, 210, 255));
				innerPanel.setBorder(BorderFactory.createTitledBorder("Group " + (g + 1)));

				for (int i = 0; i < 8; i++) {
					innerPanel.add(new JButton("G" + (g + 1) + "-B" + (i + 1)));
				}

				outerPanel.add(innerPanel);
			}

			JScrollPane scrollPane = new JScrollPane(outerPanel);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.getVerticalScrollBar().setUnitIncrement(16);

			frame.add(scrollPane);
			frame.setVisible(true);
		});
	}
}