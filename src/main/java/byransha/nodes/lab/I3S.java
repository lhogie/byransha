package byransha.nodes.lab;

import java.io.File;
import java.io.IOException;

import byransha.graph.BGraph;
import byransha.graph.BNode;

/*
 * https://codimd.math.cnrs.fr/_ivy9aRUQK2o4ue-p9RHKg?both
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class I3S extends Lab {

	public I3S(BNode g) {
		super(g);
		name.set("I3S");


		
		var lakeD = new File(g().byransha.homeDirectory, "data_lake");
		var lake = new DataLake(g(), lakeD);

		try {
			lake.load(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
