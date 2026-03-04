package byransha.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class MyLayout implements LayoutManager {
	private final Direction direction;

	public static enum Direction {
		HORIZONTAL, VERTICAL
	}

	public MyLayout(Direction d) {
		this.direction = d;
	}

	@Override
	public void addLayoutComponent(String name, Component c) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			return layoutContainer(parent, new Dimension(SwingFrontend.f.getSize().width, 400));
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return parent.getSize();
	}

	@Override
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			layoutContainer(parent, parent.getSize());
		}
	}

	public Dimension layoutContainer(Container parent, Dimension parentSize) {
		var components = parent.getComponents();
		var insets = parent.getInsets();

		var max = new Dimension();

		if (direction == Direction.VERTICAL) {
			final var startY = insets.top;
			final int heightAvailable = parentSize.height - startY - insets.bottom;
			int y = startY;
			int x = insets.left;

			for (var c : components) {
				if (!c.isVisible())
					continue;

				var ps = c.getPreferredSize();

				if (heightAvailable - y < ps.height) {
					x += ps.width;
					y = startY;
				}

				c.setBounds(x, y, ps.width, ps.height);
				y += ps.height;
				max.width = Math.max(max.width, x + ps.width);
				max.height = Math.max(max.height, y);
			}
		} else {
			final var startX = insets.left;
			final int widthAvailable = parentSize.width - startX - insets.right;
			int x = startX;
			int y = insets.top;

			for (var c : components) {
				if (!c.isVisible())
					continue;

				var ps = c.getPreferredSize();

				if (widthAvailable - x < ps.width) {
					y += ps.height;
					x = startX;
				}

				c.setBounds(x, y, ps.width, ps.height);
				x += ps.width;
				max.width = Math.max(max.width, x);
				max.height = Math.max(max.height, y + ps.height);

			}
		}

		max.width += insets.right;
		max.height += insets.bottom;
		return max;
	}
}
