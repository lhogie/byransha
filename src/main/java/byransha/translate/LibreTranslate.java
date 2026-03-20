package byransha.translate;

import java.net.HttpURLConnection;
import java.net.URL;

import byransha.graph.BGraph;

public class LibreTranslate extends Translator {

	public LibreTranslate(BGraph g) {
		super(g);
	}

	@Override
	public String googleTranslate(String text, Language from, Language to) throws Exception {
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

	@Override
	public String prettyName() {
		return "libretranslate.com";
	}
}
