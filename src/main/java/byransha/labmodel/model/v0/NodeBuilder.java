package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

public class NodeBuilder<N extends BNode> {

    public NodeInstantiationParameters parms;

    public N newInstance(BBGraph g, User u){
        try {
            return genericClass().getConstructor(BBGraph.class, User.class, NodeBuilder.class).newInstance(g, u, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<N> genericClass()
    {
        return (Class<N>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
