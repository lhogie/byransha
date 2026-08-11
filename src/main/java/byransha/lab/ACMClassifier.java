package byransha.lab;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import byransha.ID;
import byransha.graph.Hub;
import byransha.graph.LabNode;

/*
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class ACMClassifier extends LabNode {

	public static void load(Hub g, File dir) throws IOException {
		for (var l : Files.readAllLines(new File(dir, "acm_classification.txt").toPath())) {
			var a = l.split(";");

			if (a.length != 2)
				throw new IllegalStateException("invalid ACM entry: " + l);

			var n = new ACMClassifier(g, new ID());
			n.code = a[0];
			n.descr = a[1];
		}
	}

	public String code, descr;

	public ACMClassifier(Hub g, ID id) {
		super(g, id);
	}

	@Override
	public String toString() {
		return code + ": " + descr;
	}

	@Override
	public String whatIsThis() {
		return "a ACM classification";
	}

}
