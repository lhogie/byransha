package byransha;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.function.Consumer;

import byransha.event.Event;
import byransha.graph.BBGraph;
import byransha.nodes.lab.model.v0.Person;
import toools.Stop;

public class CreateNewPerson extends Event {

	String name;

	public CreateNewPerson(LocalDateTime date) {
		super(date);
	}

	public CreateNewPerson() {
		this(LocalDateTime.now());
	}

	public CreateNewPerson(String name) {
		this();
		this.name = name;
	}

	@Override
	public void undo(BBGraph g) throws Throwable {
		var luc = g.forEachNodeOfClass(Person.class, p -> Stop.stopIf(p.etatCivil.name.get().equals(name)));
		luc.delete();
	}

	@Override
	public void apply(BBGraph g) {
		var p = new Person(g);
		p.etatCivil.name.set(name);
	}

	@Override
	public void fromCSV(Iterator<String> l) {
		this.name = l.next();
	}

	@Override
	public void provideCSVElements(Consumer<String> l) {
		super.provideCSVElements(l);
		l.accept(name);
	}

}