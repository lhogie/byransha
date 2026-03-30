package byransha.ui.swing;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import byransha.graph.NodeAction;
import byransha.nodes.system.ChatNode;

public class MenuBuilder {

    public static JPopupMenu buildPopupMenu(List<? extends NodeAction> actions, ChatNode chat) {
        JPopupMenu popup = new JPopupMenu();
        // Cache of already-created submenus, keyed by their full dot-path
        Map<String, JMenu> menuCache = new LinkedHashMap<>();

        for (var action : actions) {
            String path = action.category;

            if (path == null || path.isBlank()) {
                // No category → root-level item
                popup.add(makeItem(action, chat));
            } else {
                String[] parts = path.split("\\.");
                String[] menuPath  = Arrays.copyOf(parts, parts.length - 1);
                String   leafLabel = parts[parts.length - 1];

                if (menuPath.length == 0) {
                    // Path is a single segment with no dot → also root-level
                    popup.add(makeItem(action, chat));
                } else {
                    JMenu parentMenu = getOrCreateMenuChain(popup, menuCache, menuPath);
                    parentMenu.add(makeItem(leafLabel, action, chat));
                }
            }
        }
        return popup;
    }

    // ---- helpers --------------------------------------------------------

    /** Walk (or create) the full chain of JMenus for the given path segments. */
    private static JMenu getOrCreateMenuChain(
            JPopupMenu popup, Map<String, JMenu> cache, String[] segments) {

        JMenu current = null;
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < segments.length; i++) {
            if (i > 0) key.append('.');
            key.append(segments[i]);

            String cacheKey = key.toString();
            if (cache.containsKey(cacheKey)) {
                current = cache.get(cacheKey);
            } else {
                JMenu newMenu = new JMenu(segments[i]);
                if (i == 0) {
                    popup.add(newMenu);         // top-level submenu
                } else {
                    current.add(newMenu);       // nested submenu
                }
                cache.put(cacheKey, newMenu);
                current = newMenu;
            }
        }
        return current;
    }

    private static JMenuItem makeItem(NodeAction action, ChatNode chat) {
        String[] parts = action.category == null
                ? new String[]{""} : action.category.split("/");
        return makeItem(parts[parts.length - 1], action, chat);
    }

    private static JMenuItem makeItem(String label, NodeAction action, ChatNode chat) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(e -> {
			try {
				action.exec(chat);
			} catch (Throwable e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        return item;
    }
}