package byransha.lab;

import java.io.File;

import byransha.ID;
import byransha.graph.Element;

/*
 * https://codimd.math.cnrs.fr/_ivy9aRUQK2o4ue-p9RHKg?both
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class I3S extends Lab {

	public I3S(Element g, ID id) {
		super(g, id);
		name.set("I3S");

		var lakeD = new File(hub().byransha.homeDirectory, "data_lake");

		if (lakeD.exists()) {
			var lake = new DataLake(hub(), lakeD);
		} else {
			System.out.println("data lake not found at " + lakeD.getAbsolutePath());
		}
	}
}
