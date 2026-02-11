package byransha.nodes.lab;

import byransha.BBGraph;
import byransha.nodes.BNode;
import byransha.nodes.system.User;
import byransha.nodes.system.UserApplication;
import byransha.nodes.lab.model.v0.Country;
import byransha.nodes.lab.model.v0.DataLake;
import byransha.nodes.lab.model.v0.view.LabView;
import byransha.nodes.lab.model.v0.view.StructureView;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

public class I3SApplication extends UserApplication {

    public I3SApplication(BBGraph g, User creator) {
        super(g, creator);
        new StructureView(g);
        new LabView(g);

            Country.loadCountries(g, creator);

            var lake = new DataLake(this.g, creator, Paths.get(
                    System.getProperty("user.home"),
                    "data_lake"
            ).toFile());

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
