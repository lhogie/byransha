package byransha.util;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.StringNode;

public class Translator extends BNode {
	interface TextComponentWrapper<C extends JComponent> {
		String get(Component c);

		void set(Component c, String s);
	}

	final File translateDir;

	public final StringNode targetLanguage;

	Map<Class<? extends JComponent>, TextComponentWrapper> map = new HashMap<>();

	public Translator(BGraph g) {
		super(g);

		map.put(JButton.class, new TextComponentWrapper<JButton>() {

			@Override
			public String get(Component c) {
				return ((JButton) c).getText();
			}

			@Override
			public void set(Component c, String s) {
				((JButton) c).setText(s);
			}
		});

		translateDir = new File(g.byransha.configDirectory, "translate");
		translateDir.mkdirs();

		targetLanguage = new StringNode(g, "fr", ".+");

		targetLanguage.valueChangeListeners.add((n, a, b) -> {

			var to = to();
			System.out.println("translate to " + to);

			class A {
				Set<Component> visited = new HashSet<>();

				void translateRecursively(Component c) {
					visited.add(c);
					var wrapper = map.get(c);

					if (wrapper != null) {
						var s = g.translator.translate(wrapper.get(c));

						if (s != null) {
							wrapper.set(c, s);
						}
					} else if (c instanceof Container l) {
						for (int i = 0; i < l.getComponentCount(); ++i) {
							var child = l.getComponent(i);
							if (!visited.contains(child)) {
								translateRecursively(child);
							}
						}
					}
				}
			}

			if (to != null) {
				new Thread(() -> new A().translateRecursively(g.swing.f.getContentPane())).start();
			}
		});
	}

	public enum LANG {
		fr, en, auto, es, it, de, lu
	};

	LANG to() {
		try {
			return LANG.valueOf(targetLanguage.get());
		} catch (Throwable err) {
			return null;
		}
	}

	public String translate(String s) {
		return translate(s, LANG.en, to());
	}

	public String translate(String s, LANG to) {
		return translate(s, LANG.auto, to);
	}

	public String translate(final String text, LANG from, LANG to) {
		if (text.matches("[0-9]+"))
			return text;

		var file = new File(translateDir, from + "-" + to + ".json");
		ObjectMapper mapper = new ObjectMapper();

		try {
			var node = file.exists() ? (ObjectNode) mapper.readTree(file) : new ObjectNode(factory);
			var n = node.get(text);

			if (n != null) {
				return n.asText();
			} else {
				try {
					var translated = googleTranslate(text, from, to);

					if (!text.equals(translated)) {
						node.put(text, translated);
						Files.writeString(file.toPath(), node.toPrettyString());
						return translated;
					} else {
						return null;
					}

				} catch (Exception e) {
					error(e);
					return null;
				}
			}
		} catch (IOException err) {
			error(err);
			return null;
		}

	}

	public static String libretranslate(String text, LANG from, LANG to) throws Exception {
		URL url = new URL("https://libretranslate.com/translate");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);

		String body = """
				{"q":"%s","source":"%s","target":"%s","format":"text"}
				""".formatted(text.replace("\"", "\\\""), from, to);

		conn.getOutputStream().write(body.getBytes());

		String response = new String(conn.getInputStream().readAllBytes());
		// Parse "translatedText" from JSON response
		return response.split("\"translatedText\":\"")[1].split("\"")[0];
	}

	public static String googleTranslate(String text, LANG from, LANG to) throws Exception {
		String URL = "https://translate.googleapis.com/translate_a/single" + "?client=gtx&sl=%s&tl=%s&dt=t&q=%s";

		String encoded = java.net.URLEncoder.encode(text, "UTF-8");
		String url = URL.formatted(from, to, encoded);

		HttpURLConnection conn = (HttpURLConnection) new java.net.URL(url).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36");

		String response = new String(conn.getInputStream().readAllBytes());

		// Response is a nested JSON array — extract first translation
		// Format: [[["translatedText","originalText",...],...],...]
		return response.split("\"")[1];
	}

	public static void main(String[] args) throws Exception {
		System.out.println(googleTranslate("What do you want to do?", LANG.auto, LANG.fr));
	}

	@Override
	public String whatIsThis() {
		// TODO Auto-generated method stub
		return "translator";
	}

	@Override
	public String prettyName() {
		return "Google translate";
	}
}
