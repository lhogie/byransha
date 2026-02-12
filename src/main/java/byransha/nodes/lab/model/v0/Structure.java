package byransha.nodes.lab.model.v0;

import byransha.nodes.system.User;
import byransha.graph.BBGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;

public class Structure extends BusinessNode {
    public StringNode name;
    public ListNode<Structure> subStructures;
    public ListNode<Office> offices;

    public Structure(BBGraph g, User creator) {
        super(g, creator);
        name = new StringNode(g, creator, "", ".+");
        subStructures = new ListNode(g, creator);
        offices =new ListNode(g, creator);
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
