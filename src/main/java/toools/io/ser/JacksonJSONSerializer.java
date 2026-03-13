package toools.io.ser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonJSONSerializer<E> extends Serializer<E> {
	public static final JacksonJSONSerializer instance = new JacksonJSONSerializer();

	private ObjectMapper objectMapper = new ObjectMapper();

	public JacksonJSONSerializer() {
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator());
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
				ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);
	}

	@Override
	public E read(InputStream is) throws IOException {
		return (E) objectMapper.readValue(is, Object.class);
	}

	@Override
	public void write(E o, OutputStream os) throws IOException {
		objectMapper.writeValue(os, o);
	}

	@Override
	public String getMIMEType() {
		return "Jackson JSON";
	}

	public static void main(String[] args) {
		List o = new ArrayList<>();
		o.add(4);
		o.add("coucou");
		o.add(new Vector<>());

		System.out.println(o);

		var b = instance.toBytes(o);
		System.out.println(new String(b));
		System.out.println(instance.fromBytes(b));
	}

	@Override
	public boolean isBinary() {
		return false;
	}

}
