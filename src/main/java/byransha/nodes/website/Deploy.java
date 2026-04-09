package byransha.nodes.website;

import java.nio.file.Files;

import byransha.graph.Category;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.primitive.FileNode;
import byransha.nodes.system.ChatNode;

public class Deploy extends NodeAction<Website, FileNode> {
	FileNode directory;

	public static class website extends Category {
	}

	public Deploy(Website website) {
		super(website.g, website, website.class);
	}

	@Override
	public String whatItDoes() {
		return "deploy the website";
	}

	@Override
	public ActionResult exec(ChatNode chat) throws Throwable {
		Files.writeString(directory.file.toPath(), inputNode.toHTMLPage().toHTML());
		return createResultNode(directory, false);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}
