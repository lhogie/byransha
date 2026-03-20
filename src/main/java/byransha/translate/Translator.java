package byransha.translate;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.StringNode;
import byransha.ui.swing.Translatable;

public class Translator extends BNode {
	public enum Language {
		fr, en, auto, es, it, de, lu
	};

	final File translateDir;
	public final StringNode targetLanguage;
	List<Dictionnary> dictionaries = new ArrayList<>();

	public Translator(BGraph g) {
		super(g);
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					error(e);
				}

				for (var dict : dictionaries) {
					if (dict.needSave) {
						try {
							synchronized (this) {
								dict.save();
							}
						} catch (IOException err) {
							error(err);
						}
					}
				}
			}
		}).start();

		translateDir = new File(g.byransha.configDirectory, "translate");
		translateDir.mkdirs();

		targetLanguage = new StringNode(g, "fr", ".+");

		targetLanguage.valueChangeListeners.add((n, a, b) -> {
			if (userDefinedTargetLanguage() != null) {
				new Thread(() -> translateRecursively(g.swing.f.getContentPane(), new HashSet<>())).start();
			}
		});
	}

	private void translateRecursively(Component c, Set<Component> visited) {
		visited.add(c);

		if (c instanceof Translatable tr) {
			String initialText = tr.getText();
			var translated = translate(initialText);

			if (translated != null) {
				tr.setText(translated);
			}
		} else if (c instanceof Container l) {
			for (int i = 0; i < l.getComponentCount(); ++i) {
				var child = l.getComponent(i);
				if (!visited.contains(child)) {
					translateRecursively(child, visited);
				}
			}
		}
	}

	Language userDefinedTargetLanguage() {
		try {
			return Language.valueOf(targetLanguage.get());
		} catch (Throwable err) {
			return null;
		}
	}

	public String translate(String s) {
		return translate(s, Language.en, userDefinedTargetLanguage());
	}

	public String translate(String s, Language to) {
		return translate(s, Language.auto, to);
	}

	public synchronized String translate(final String originalText, Language from, Language to) {
		if (originalText.isBlank() || originalText.matches("[0-9]+"))
			return null;

		var dictionary = dictionaries.stream().filter(d -> d.from == from && d.to == to).findFirst()
				.orElseGet(() -> null);

		if (dictionary == null) {
			try {
				var file = new File(translateDir, from + "-" + to + ".json");
				dictionary = new Dictionnary(from, to, file);
				dictionaries.add(dictionary);
			} catch (IOException err) {
				error(err);
				return null;
			}
		}

		var translated = dictionary.lookup(originalText);

		try {
			translated = googleTranslate(originalText, from, to);

			if (!originalText.equals(translated)) {
				dictionary.put(originalText, translated);
				return translated;
			} else {
				return null;
			}

		} catch (Exception e) {
			error(e);
			return null;
		}
	}

	public static String libretranslate(String text, Language from, Language to) throws Exception {
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

	public static String googleTranslate(String text, Language from, Language to) throws Exception {
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

	@Override
	public String whatIsThis() {
		return "translator";
	}

	@Override
	public String prettyName() {
		return "Google translate";
	}
}
