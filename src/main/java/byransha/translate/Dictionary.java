package byransha.translate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.translate.Translator.Language;

public class Dictionary {
	private final ObjectNode jsonNode;
	boolean needSave = false;
	final File file;
	public final Language from, to;

	public Dictionary(Language from, Language to, File file) throws IOException {
		this.from = from;
		this.to = to;
		this.file = file;

		if (file.exists()) {
			System.err.println("reading: " + file);
			this.jsonNode = (ObjectNode) new ObjectMapper().readTree(file);
			System.out.println(jsonNode.toPrettyString());
		} else {
			System.err.println("not found: " + file);
			this.jsonNode = new ObjectNode(Translator.factory);
		}
	}

	public void put(String originalText, String translation) {
		jsonNode.put(originalText, translation);
		needSave = true;
	}

	public String lookup(String s) {
		var n = jsonNode.get(s);
		return n == null ? s : n.asText();
	}

	public void save() throws IOException {
		System.err.println("saving " + file);
		Files.writeString(file.toPath(), jsonNode.toPrettyString());
		needSave = false;
	}
}