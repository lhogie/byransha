package byransha;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import toools.text.TextUtilities;

public abstract class ValuedNode<V> extends PersistingNode {
	V value;

	public ValuedNode(BBGraph db) {
		super(db);
	}

	public ValuedNode(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public String toString() {
		return super.toString() + "(value=\"" + get() + "\")";
	}

	@Override
	public void forEachOut(BiConsumer<String, BNode> consumer) {
	}

	public abstract void fromString(String s);

	@Override
	public int distanceToSearchString(String searchString) {
		return TextUtilities.computeLevenshteinDistance(searchString, get().toString());
	}

	public V get() {
		if (value == null) {
			try {
				loadValue(f -> System.out.println("loading value for " + id()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return value;
	}

	public void set(V newValue) {
		this.value = newValue;

		if (directory() != null) {
			saveValue(BBGraph.sysoutPrinter);
		}
	}

	public void saveValue(Consumer<File> writingFiles) {
		var valueFile = new File(directory(), "value.txt");
		var dir = valueFile.getParentFile();

		if (!dir.exists()) {
			writingFiles.accept(dir);
			dir.mkdirs();
		}

		writingFiles.accept(valueFile);

		try {
//			System.out.println(graph.findRefsTO(this));
			if (value == null) {
				if (valueFile.exists()) {
					valueFile.delete();
				}
			} else {
				Files.write(valueFile.toPath(), value.toString().getBytes());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void loadValue(Consumer<File> readingFiles) throws IOException {
		var valueFile = new File(directory(), "value.txt");

		if (valueFile.exists()) {
			readingFiles.accept(valueFile);
			byte[] bytes = Files.readAllBytes(valueFile.toPath());
			fromString(new String(bytes));
		}
	}
}
