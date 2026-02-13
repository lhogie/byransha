package byransha.use_case.lab;

import java.io.IOException;
import java.nio.file.Paths;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.lab.model.v0.Country;
import byransha.nodes.lab.model.v0.DataLake;
import byransha.nodes.lab.model.v0.view.LabView;
import byransha.nodes.lab.model.v0.view.StructureView;
import byransha.nodes.system.User;
import byransha.nodes.system.UserApplication;

public class I3SApplication extends UserApplication {

	public I3SApplication(BBGraph g, User creator) {
		super(g);
		new StructureView(g);
		new LabView(g);

		Country.loadCountries(g, creator);

		var lake = new DataLake(this.g, creator, Paths.get(System.getProperty("user.home"), "data_lake").toFile());

		try {
			lake.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Class<? extends BNode> rootNodeClass() {
		return I3S.class;
	}
}
