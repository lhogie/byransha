package byransha.ui.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.util.Base62;
import byransha.util.PossiblyFailingConsumer;

public class Utils {

	public static JScrollPane getScrollPane(JComponent c) {
		var parent = c.getParent();

		while (true) {
			if (parent == null) {
				return null;
			}

			if (parent instanceof JScrollPane sc) {
				return sc;
			}

			parent = parent.getParent();
		}
	}

	public static void idDropTarget(BGraph g, JComponent c, PossiblyFailingConsumer<BNode> dropAction) {
		new DropTarget(c, new DropTargetAdapter() {
			@Override
			public void drop(DropTargetDropEvent e) {
				try {
					String text = (String) e.getTransferable().getTransferData(DataFlavor.stringFlavor);
					long id = Base62.decode(text);
					var droppedNode = g.indexes.byId.get(id);

					try {
						e.acceptDrop(DnDConstants.ACTION_COPY);
						dropAction.accept(droppedNode);
					} catch (Throwable err) {
						e.rejectDrop();
					}

					e.dropComplete(true);
				} catch (Throwable ex) {
					e.dropComplete(false);
					g.error(ex);
				}
			}
		});
	}

	public static ResizableByGrip resizableScrollPane(JComponent p) {
		var sp = new JScrollPane(p);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setPreferredSize(new Dimension(500, 100));
		var rbg = new ResizableByGrip(sp);
		return rbg;
	}

	public static void setUIFont(FontUIResource f) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, f);
			}
		}
	}

	public static final int chatWidth;
	public static final Dimension initialSize;
	public static final Point initialLocation;
	public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	static {
		chatWidth = (9 * screenSize.height) / 16;
		initialSize = new Dimension(chatWidth, screenSize.height);
		initialLocation = new Point((screenSize.width - chatWidth) / 2, 0);
	}

	public static Icon icon(String s, double scaleFactor) {
		var i = new ImageIcon(Utils.class.getResource("icon/" + s + ".png"));

		if (scaleFactor != 1) {
			var img = i.getImage();
			var newImg = img.getScaledInstance((int) (img.getWidth(null) * scaleFactor),
					(int) (img.getHeight(null) * scaleFactor), java.awt.Image.SCALE_SMOOTH);
			i = new ImageIcon(newImg);
		}

		return i;
	}

}
