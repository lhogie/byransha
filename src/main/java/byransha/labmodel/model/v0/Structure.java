package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.StringNode;

public class Structure extends BusinessNode {
    public StringNode name;
    public ListNode<Structure> subStructures;
    public ListNode<Status> status;
    public ListNode<Office> offices;

    public Structure(BBGraph g) {
        super(g);
        name = g.create(  StringNode.class);
        subStructures = g.create(  ListNode.class);
        status = g.create( ListNode.class);
        offices = g.create(  ListNode.class);
    }

    public Structure(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public String whatIsThis() {
        return "a physical structure having members, offices, sub-structures";
    }

    public double occupationRatio() {
        return offices
            .getElements()
            .stream()
            .mapToDouble(Office::occupationRatio)
            .average()
            .getAsDouble();
    }

    public double avgSurfacePerUser() {
        return offices
            .getElements()
            .stream()
            .mapToDouble(Office::surfacePerUser)
            .average()
            .getAsDouble();
    }

    public double totalSurface() {
        return offices
            .getElements()
            .stream()
            .mapToDouble(o -> o.surface.get())
            .sum();
    }

    @Override
    public String prettyName() {
        if (name == null || name.get() == null || name.get().isEmpty()) {
            return null;
        }
        return name.get();
    }
}
