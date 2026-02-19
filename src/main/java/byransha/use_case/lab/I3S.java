package byransha.use_case.lab;

import java.io.IOException;
import java.nio.file.Paths;

import byransha.graph.BBGraph;
import byransha.nodes.lab.model.v0.Country;
import byransha.nodes.lab.model.v0.DataLake;
import byransha.nodes.lab.model.v0.Lab;

/*
 * https://codimd.math.cnrs.fr/_ivy9aRUQK2o4ue-p9RHKg?both
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class I3S extends Lab {

	public I3S(BBGraph g) {
		super(g);
		name.set("I3S");
		Country.loadCountries(g);

		var lake = new DataLake(this.g, Paths.get(System.getProperty("user.home"), "data_lake").toFile());

		try {
			lake.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
