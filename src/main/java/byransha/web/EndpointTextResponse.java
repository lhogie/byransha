package byransha.web;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class EndpointTextResponse extends EndpointResponse<String> {

	public interface A extends Consumer<PrintWriter> {
		default String writer2string() {
			var sw = new StringWriter();
			var pw = new PrintWriter(sw);
			accept(pw);
			return sw.toString();
		}
	}

	public EndpointTextResponse(String textMimeType, A pw) {
		super(pw.writer2string(), textMimeType);
	}

	@Override
	public JsonNode data() {
		return new TextNode(data.replaceAll("\n", "\n"));
	}

	@Override
	public String toRawText() {
		return data;
	}

}
