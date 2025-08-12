package byransha.labmodel;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.UserApplication;
import byransha.labmodel.model.v0.Agent;
import byransha.labmodel.model.v0.Country;
import byransha.labmodel.model.v0.DataLake;
import byransha.labmodel.model.v0.view.LabView;
import byransha.labmodel.model.v0.view.StructureView;
import java.io.IOException;
import java.nio.file.Paths;

public class I3SApplication extends UserApplication {

    public I3SApplication(BBGraph g, User creator) {
        super(g, creator);
        new StructureView(g);
        new LabView(g);
        new Agent(g, creator);
        Country.loadCountries(g, creator);
        var lake = new DataLake(g, creator);
        lake.inputDir = Paths.get(
            System.getProperty("user.home"),
            "i3s_extraction"
        ).toFile();

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
