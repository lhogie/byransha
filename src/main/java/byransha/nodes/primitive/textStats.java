package byransha.nodes.primitive;

import byransha.graph.Category.statistics;
import byransha.graph.Category.text;
import byransha.graph.list.action.FunctionAction;

public class textStats extends FunctionAction<TextNode, TextNode> {

	public textStats(TextNode inputNode) {
		super(inputNode, text.class, statistics.class);
	}

	@Override
	public String whatItDoes() {
		return "stats about the text, like number of lines, words, characters, etc.";
	}

	@Override
	protected void impl() throws Throwable {
		String text = inputNode.get();
		int lines = text.split("\r\n|\r|\n").length;
		int words = text.split("\\s+").length;
		int characters = text.length();

		result = new TextNode(g, "text stats",
				String.format("lines: %d\nwords: %d\ncharacters: %d", lines, words, characters));
	}

	@Override
	public boolean applies() {
		return true;
	}

}
