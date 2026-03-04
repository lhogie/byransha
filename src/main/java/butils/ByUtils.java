package butils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BNode;

public class ByUtils {
	public static final Map<Class, Integer> sizeOfPrimitive = new HashMap();

	static {
		sizeOfPrimitive.put(long.class, 8);
		sizeOfPrimitive.put(int.class, 4);
		sizeOfPrimitive.put(short.class, 2);
		sizeOfPrimitive.put(byte.class, 1);
		sizeOfPrimitive.put(char.class, 1);
		sizeOfPrimitive.put(boolean.class, 1);
		sizeOfPrimitive.put(float.class, 4);
		sizeOfPrimitive.put(double.class, 8);
	}

	public static String camelToWords(String text) {
	    if (text == null || text.isBlank()) return text;

	    // 1. Ajoute un espace avant chaque majuscule
	    String result = text.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
	    
	    // 2. Met la première lettre en majuscule (Optionnel)
	    return result.substring(0, 1).toUpperCase() + result.substring(1);
	}
	
	public static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		try {
			var field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException e) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass == null) {
				throw new NoSuchFieldException("Field '" + fieldName + "' not found in class " + clazz.getName());
			}
			return findField(superClass, fieldName);
		}
	}

	public static String mimeType(String url) {
		if (url == null)
			return "application/octet-stream";

		String lowerUrl = url.toLowerCase();

		if (lowerUrl.endsWith(".html") || lowerUrl.endsWith(".htm")) {
			return "text/html; charset=utf-8";
		} else if (lowerUrl.endsWith(".js") || lowerUrl.endsWith(".jsx")) {
			return "text/javascript; charset=utf-8";
		} else if (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (lowerUrl.endsWith(".png")) {
			return "image/png";
		} else if (lowerUrl.endsWith(".svg")) {
			return "image/svg+xml";
		} else if (lowerUrl.endsWith(".css")) {
			return "text/css; charset=utf-8";
		} else if (lowerUrl.endsWith(".json")) {
			return "application/json; charset=utf-8";
		} else if (lowerUrl.endsWith(".txt")) {
			return "text/plain; charset=utf-8";
		} else if (lowerUrl.endsWith(".ico")) {
			return "image/x-icon";
		} else if (lowerUrl.endsWith(".webmanifest") || lowerUrl.endsWith(".manifest")) {
			return "application/manifest+json";
		} else if (lowerUrl.endsWith(".ttf")) {
			return "font/ttf";
		} else if (lowerUrl.endsWith(".woff")) {
			return "font/woff";
		} else if (lowerUrl.endsWith(".woff2")) {
			return "font/woff2";
		} else {
			System.err.println(
					"Warning: Unknown MIME type for file: " + url + ". Defaulting to application/octet-stream.");
			return "application/octet-stream";
		}
	}

	public static class JsonToTreeConverter {
		public static JTree buildTreeModel(JsonNode rootNode) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
			addNodes(root, rootNode);
			return new JTree(new DefaultTreeModel(root));
		}

		private static void addNodes(DefaultMutableTreeNode parent, JsonNode node) {
			if (node.isObject()) {
				Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

				while (fields.hasNext()) {
					Map.Entry<String, JsonNode> field = fields.next();
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(field.getKey());
					parent.add(child);
					addNodes(child, field.getValue()); // Recursion
				}
			} else if (node.isArray()) {
				for (int i = 0; i < node.size(); i++) {
					DefaultMutableTreeNode child = new DefaultMutableTreeNode("[" + i + "]");
					parent.add(child);
					addNodes(child, node.get(i)); // Recursion
				}
			} else {
				// It's a leaf node (String, Number, Boolean)
				parent.setUserObject(parent.getUserObject() + ": " + node.asText());
			}
		}
	}

	public static void ensure(boolean b) {
		if (!b)
			throw new IllegalStateException();
	}
	
	public static String ms2string(long ms) {
		if (ms < 1000) {
			return ms + "ms";
		} else if (ms < 60 * 1000) {
			return String.format("%.2fs", ms / 1000.0);
		} else if (ms < 60 * 60 * 1000) {
			return String.format("%.2fm", ms / (60 * 1000.0));
		} else {
			return String.format("%.2fh", ms / (60 * 60 * 1000.0));
		}
	}
	public static int sizeOfObject(Object o) {
		if (o == null) {
			return 0;
		} else if (o instanceof BNode) {
			return 0;
		} else if (o.getClass().isArray()) {
			var componentType = o.getClass().getComponentType();
			var arrayLen = Array.getLength(o);

			if (componentType.isPrimitive()) {
				return arrayLen * butils.ByUtils.sizeOfPrimitive.get(componentType);
			} else {
				int sum = 0;

				for (int i = 0; i < arrayLen; ++i) {
					sum += 4 + sizeOfObject(Array.get(o, i));
				}

				return sum;
			}
		} else if (o instanceof Iterable iter) {
			int sum = 4; // nb of elements

			for (var ce : iter) {
				sum += 4 + sizeOfObject(ce);
			}

			return sum;
		} else if (o instanceof LocalDateTime) {
			return 76;
		} else if (o instanceof Map m) {
			return 4 + sizeOfObject(m.keySet()) + 4 + sizeOfObject(m.values());
		} else {
			return sizeOfFields(o);
		}
	}

	public static int sizeOfFields(Object o) {
		int totalSize = 0;

		for (Class c = o.getClass(); c != null; c = c.getSuperclass()) {
			for (var field : c.getDeclaredFields()) {
				if ((field.getModifiers() & Modifier.STATIC) == 0) { // non static
					var fieldDeclaraionType = field.getType();

					if (fieldDeclaraionType.isPrimitive()) {
						totalSize += butils.ByUtils.sizeOfPrimitive.get(fieldDeclaraionType);
					} else {
						totalSize += 4; // ref size

						try {
							field.setAccessible(true);
							totalSize += sizeOfObject(field.get(o));
						} catch (Throwable err) {
							// throw err instanceof RuntimeException re ? re : new RuntimeException(err);
						}
					}
				}
			}
		}

		return totalSize;

	}
}
