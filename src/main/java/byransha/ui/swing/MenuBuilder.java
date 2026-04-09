package byransha.ui.swing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import byransha.graph.NodeAction;
import byransha.nodes.system.ChatNode;

public class MenuBuilder {

	public static JPopupMenu buildPopupMenu(List<? extends NodeAction> actions, ChatNode chat) {
		Collections.reverse(actions);
		UIManager.put("MenuItem.selectionBackground", Color.red);
		UIManager.put("MenuItem.selectionForeground", Color.WHITE);
		UIManager.put("Menu.selectionBackground", Color.red);
		UIManager.put("Menu.selectionForeground", Color.WHITE);
		JPopupMenu popup = new JPopupMenu();

		Map<String, JMenu> menus = new HashMap<>();

		for (var a : actions) {
			if (a.category == null) {
				popup.add(makeItem(a, chat));
			} else {
				var segments = new ArrayList<>(List.of(a.category.split("/")));
				menu(popup, segments, menus).add(makeItem(a, chat));
			}
		}

		sort(popup);
		return popup;
	}

	private static void sort(JComponent c) {
		var l = Arrays.stream(c.getComponents()).map(cc -> (AbstractButton) cc)
				.sorted((a, b) -> a.getText().compareTo(b.getText())).toList();
//		l.forEach(cc -> sort(cc));
		c.removeAll();
		l.forEach(cc -> c.add(cc));
	}

	private static JMenu menu(JPopupMenu popup, List<String> segments, Map<String, JMenu> menus) {
		final var path = String.join("/", segments);
		var m = menus.get(path);

		if (m == null) {
			menus.put(path, m = new JMenu(segments.removeLast()));

			if (segments.isEmpty()) {
				popup.add(m);
			} else {
				menu(popup, segments, menus).add(m);
			}
		}

		return m;
	}

	private static JMenuItem makeItem(NodeAction a, ChatNode chat) {
		var i = new JMenuItem(a.whatItDoes());
		i.setEnabled(a.applies(chat));
		i.addActionListener(e -> {
			chat.append(a);
		});
		return i;
	}

}