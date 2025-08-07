package byransha;

import java.lang.reflect.Modifier;
import java.util.List;
import toools.reflect.Clazz;

public class ClassNode<T extends BNode> extends BNode {

    public final Class<T> typeOfCluster;
    private ListNode<T> instances;
    public ListNode<FieldNode> fields;

    public ClassNode(BBGraph g, Class<T> typeOfCluster) {
        super(g);
        //this.setColor("#9900ff");
        this.typeOfCluster = typeOfCluster;

        for (var c : Clazz.bfs(typeOfCluster)) {
            for (var f : c.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) != 0) continue;

                if (BNode.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    var fieldNode = g.create(FieldNode.class);
                    fieldNode.set(f);
                    fieldNode.name = g.create(StringNode.class);
                    fieldNode.name.set(f.getName());
                    fields.add(fieldNode);
                }
            }
        }
    }

    public Class<T> getTypeOfCluster() {
        return this.typeOfCluster;
    }

    @Override
    public String whatIsThis() {
        return "a cluster to group nodes together";
    }

    @Override
    public String prettyName() {
        return "class " + this.typeOfCluster;
    }

    public List<T> instances() {
        return instances.getElements();
    }
}
