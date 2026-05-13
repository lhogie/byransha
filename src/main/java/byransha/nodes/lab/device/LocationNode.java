package byransha.nodes.lab.device;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import byransha.graph.BNode;
import byransha.nodes.primitive.ValuedNode;

public class LocationNode extends ValuedNode<GPSLocation> {

	public LocationNode(BNode parent) {
		super(parent);
	}

	@Override
	public GPSLocation defaultValue() {
		return null;
	}

	@Override
	protected void writeValue(GPSLocation v, ObjectOutput out) throws IOException {
		out.writeDouble(v.lattitude);
		out.writeDouble(v.longitude);
	}

	@Override
	protected GPSLocation readValue(ObjectInput in) throws IOException {
		var l = new GPSLocation();
		l.lattitude = in.readDouble();
		l.longitude = in.readDouble();
		return l;
	}

}
