package byransha.labmodel;

import byransha.BBGraph;
import byransha.BNode;
import byransha.UserApplication;
import byransha.labmodel.model.v0.Agent;
import byransha.labmodel.model.v0.Country;
import byransha.labmodel.model.v0.DataLake;
import byransha.labmodel.model.v0.view.LabView;
import byransha.labmodel.model.v0.view.StructureView;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;

public class I3SApplication extends UserApplication {

    public I3SApplication(BBGraph g){
super(g);
        g.create( StructureView.class);
        g.create( LabView.class);
        g.create( Agent.class);
        Country.loadCountries(g);
        var lake = g.create( DataLake.class);
        lake.inputDir = new File("~/i3s_extraction");

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
