package byransha.nodes.system;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.graph.BGraph;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.URLNode;
import byransha.util.ByUtils;

public class Byransha extends SystemNode {
	public final StringNode versionNode;
	public final URLNode sourceRepoURL;
	public final File configDirectory;
	public final StringNode exeFile;
	public static final String VERSION = "0.0.1";
	public static final String downloadExeURL = "https://webusers.i3s.unice.fr/~hogie/software/byransha/";
	public static byte[] currentExeBytes = "".getBytes();

	public Byransha(BGraph g) {
		super(g);
		exeFile = new StringNode(g);
		var classpath = System.getProperty("java.class.path").split(File.pathSeparator);

		if (classpath.length == 1) {
			exeFile.set(classpath[0]);
		} else {
			exeFile.set("development classpath");
		}

		versionNode = new StringNode(g, VERSION, ".*");
		sourceRepoURL = new URLNode(g, "https://github.com/lhogie/byransha");
		configDirectory = new File(ByUtils.home, ".byransha");
		var exeFile = new File(configDirectory, "byransha.jar");

		try {
			currentExeBytes = exeFile.exists() ? Files.readAllBytes(exeFile.toPath()) : "".getBytes();

			new Thread(() -> {
				while (true) {
					try {
						var exeBytes = new URL(downloadExeURL).openStream().readAllBytes();

						if (!Arrays.equals(currentExeBytes, exeBytes)) {
							Files.write(exeFile.toPath(), exeBytes);
							currentExeBytes = exeBytes;
							versionNode.set(versionNode.get() + " - new version installed");
						}

						Thread.sleep(60);
					} catch (IOException | InterruptedException e) {
						error(e);
					}
				}

			}, "check new version thread").start();

		} catch (IOException err) {
			error(err);
		}
	}

	@Override
	public void createActions() {
		cachedActions.add(new Restart(g, this));
		super.createActions();
	}

	@Override
	public String prettyName() {
		return "Byransha";
	}

	@Override
	public String whatIsThis() {
		return "Byransha";
	}

	interface JSONable {
		JsonNode toJson();
	}

	public static class Distribution<E extends Comparable<E>> extends CoupleList<E, Double> {

		public void addOccurence(E a) {
			var e = getEntry(a);

			if (e == null) {
				e = addXY(a, 0d);
			}

			e.y = e.y + 1;
		}
	}

	public static class Function extends CoupleList<Double, Double> {
	}

	public static class CoupleList<X extends Comparable<X>, Y> implements JSONable {
		static class Couple<X extends Comparable<X>, Y> implements JSONable, Comparable<Couple<X, Y>> {
			final X x;
			public Y y;

			public Couple(X x, Y y) {
				this.x = x;
				this.y = y;
			}

			@Override
			public JsonNode toJson() {
				var n = new ObjectNode(null);
				n.set(x.toString(), new TextNode(y.toString()));
				return n;
			}

			@Override
			public int compareTo(Couple<X, Y> o) {
				return x.compareTo(o.x);
			}
		}

		final List<Couple<X, Y>> entries = new ArrayList<>();

		@Override
		public JsonNode toJson() {
			Collections.sort(entries);
			var n = new ArrayNode(null);
			entries.forEach(e -> n.add(e.toJson()));
			return n;
		}

		public Couple<X, Y> getEntry(X x) {
			for (var e : entries) {
				if (e.x.equals(x)) {
					return e;
				}
			}

			return null;
		}

		public Couple<X, Y> addXY(X x, Y y) {
			if (getEntry(x) != null)
				throw new IllegalStateException(x + " was already defined");

			var e = new Couple<>(x, y);
			entries.add(e);
			return e;
		}

	}

}
