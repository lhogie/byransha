package byransha.ui.swing;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;

// Scrollable panel: tells JScrollPane to match viewport width,
// so the BoxLayout children reflow based on the visible width
public class ScrollablePanel extends JPanel implements Scrollable {
	public ScrollablePanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 20;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 60;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true; // match viewport width so WrapPanels reflow correctly
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false; // allow vertical scrolling
	}
}