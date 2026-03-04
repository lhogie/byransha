package byransha.nodes.lab;

import java.io.File;
import java.io.IOException;

import byransha.graph.BGraph;

/*
 * https://codimd.math.cnrs.fr/_ivy9aRUQK2o4ue-p9RHKg?both
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class I3S extends Lab {

	public I3S(BGraph g) {
		super(g);
		name.set("I3S");

		var home = new File(System.getProperty("user.home"));
		var lakeD = new File(home, "a/job/byransha/data_lake");
		var lake = new DataLake(this.g, lakeD);

		try {
			lake.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
