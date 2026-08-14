package lab;

import java.io.File;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;

/*
 * https://codimd.math.cnrs.fr/_ivy9aRUQK2o4ue-p9RHKg?both
 * https://cran.r-project.org/web/classifications/ACM.html
 */

public class I3S extends Lab {

	public static final File dataLakeDirecory = new File(System.getProperty("user.home"), "i3s_data_lake");

	@ShowInKishanView
	DataLake lake = dataLakeDirecory.exists() ? new DataLake(this, dataLakeDirecory) : null;

	public I3S(Element g, ID id) {
		super(g, id);
		name.set("I3S");
	}
}
