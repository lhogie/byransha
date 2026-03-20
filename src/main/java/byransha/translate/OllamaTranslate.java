package byransha.translate;

import byransha.ai.OllamaModel;
import byransha.graph.BGraph;

public class OllamaTranslate extends Translator {

	public OllamaTranslate(BGraph g) {
		super(g);
	}

	@Override
	public String googleTranslate(String text, Language from, Language to) throws Exception {
		return OllamaModel.chat("please translate the following text from " + from + " to " + to + ":\n\n" + text);
	}

	@Override
	public String prettyName() {
		return "libretranslate.com";
	}
}
