package byransha.ui.swing;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

public class Utils {
	public static ResizableByGrip resizableScrollPane(JComponent p) {
		var sp = new JScrollPane(p);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setPreferredSize(new Dimension(500, 100));
		var rbg = new ResizableByGrip(sp);
		return rbg;
	}
}
