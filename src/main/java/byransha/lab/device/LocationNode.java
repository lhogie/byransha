package byransha.lab.device;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import byransha.Element;
import byransha.ID;
import byransha.primitive.ValuedElement;

public class LocationNode extends ValuedElement<GPSLocation> {

	public LocationNode(Element parent, ID id) {
		super(parent, id);
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
