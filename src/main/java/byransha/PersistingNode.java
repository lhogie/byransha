package byransha;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public abstract class PersistingNode extends BNode {

	public PersistingNode(BBGraph g) {
		super(g);
	}

	public PersistingNode(BBGraph g, int id) {
		super(g, id);
	}

	public File directory() {
		if (graph == null)
			return null;

		if (graph.directory == null)
			return null;

		return new File(graph.directory, getClass().getName() + "/." + id());
	}

	public File outsDirectory() {
		var d = directory();
		return d == null ? null : new File(directory(), "outs");
	}

	public void saveOuts(Consumer<File> writingFiles, String id) {
		var outD = outsDirectory();

		if (!outD.exists()) {
			writingFiles.accept(outD);
			outD.mkdirs();
		}

		forEachOut((name, outNode) -> {
			try {
				var symlink = new File(outD, name + id);// + "@" + outNode.id());

				for (var e : outD.listFiles()) {
					if (e.getName().equals(symlink.getName())) {
//						System.err.println("Symlink with same name already exists outs: " + symlink.getName());
						return;
					}
				}
				if (symlink.exists()) {
					symlink.delete();
				}
				if(outNode instanceof PersistingNode) {
					writingFiles.accept(symlink);
					Files.createSymbolicLink(symlink.toPath(), ((PersistingNode) outNode).directory().toPath());

				}
				else{
					writingFiles.accept(symlink);
					Files.createSymbolicLink(symlink.toPath(), directory().toPath());
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void createOutSymLinks(Consumer<File> writingFiles) {
		saveOuts(writingFiles, "");
	}

	public void createInSymLinks(Consumer<File> writingFiles) {
		var inD = new File(directory(), "ins");

		if (!inD.exists()) {
			writingFiles.accept(inD);
			inD.mkdirs();
		}

		forEachIn((name, inNode) -> {
			if (inNode instanceof PersistingNode pin) {
				try {
					var symlink = new File(inD, inNode + "." + name);

					for (var e : inD.listFiles()) {
						if (e.getName().equals(symlink.getName())) {
//							System.err.println("Symlink with same name already exists ins: " + symlink.getName());
							return;
						}
					}

					writingFiles.accept(symlink);
					System.err.println(symlink.toPath());
					System.err.println(pin.directory().toPath());
					Files.createSymbolicLink(symlink.toPath(), pin.directory().toPath());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public void save(Consumer<File> writingFiles) {
		createOutSymLinks(writingFiles);
		createInSymLinks(writingFiles);
	}

}
