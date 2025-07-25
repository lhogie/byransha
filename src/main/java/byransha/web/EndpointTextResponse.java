package byransha.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

public class EndpointTextResponse extends EndpointResponse<String> {

    private static String ensureUtf8Charset(String contentType) {
        if (contentType == null) {
            return contentType;
        }

        // Add charset=utf-8 to text content types if not already present
        if (
            contentType.startsWith("text/") && !contentType.contains("charset")
        ) {
            return contentType + "; charset=utf-8";
        }

        return contentType;
    }

    public interface A extends Consumer<PrintWriter> {
        default String writer2string() {
            var sw = new StringWriter();
            var pw = new PrintWriter(sw);
            accept(pw);
            return sw.toString();
        }
    }

    public EndpointTextResponse(String textMimeType, A pw) {
        super(pw.writer2string(), ensureUtf8Charset(textMimeType));
    }

    public EndpointTextResponse(String textMimeType, A pw, int statusCode) {
        super(pw.writer2string(), ensureUtf8Charset(textMimeType), statusCode);
    }

    public EndpointTextResponse(String textMimeType, String text) {
        super(text, ensureUtf8Charset(textMimeType));
    }

    public EndpointTextResponse(
        String textMimeType,
        String text,
        int statusCode
    ) {
        super(text, ensureUtf8Charset(textMimeType), statusCode);
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
