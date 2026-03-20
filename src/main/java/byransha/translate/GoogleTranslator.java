package byransha.translate;

import java.net.HttpURLConnection;

import byransha.graph.BGraph;

public class GoogleTranslator extends Translator {

	public GoogleTranslator(BGraph g) {
		super(g);
	}

	@Override
	public String googleTranslate(String text, Language from, Language to) throws Exception {
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
	public String prettyName() {
		return "Google Translate";
	}
}
