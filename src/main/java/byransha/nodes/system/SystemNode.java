package byransha.nodes.system;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import byransha.event.EventListOneFilePerDay;
import byransha.graph.BBGraph;
import byransha.swing.SwingFrontend;
import byransha.web.WebServer;

public class SystemNode extends SystemB {
	public final User admin, guest;
	public final UserApplication application;
	public final JVMNode jvm;
	public final Byransha byransha;
	public final OSNode os;
	public final EventListOneFilePerDay eventList;
	public WebServer webServer;
	public SwingFrontend swing;

	public SystemNode(BBGraph g, Class<? extends UserApplication> appClass)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		super(g);
		this.application = appClass.getConstructor(BBGraph.class).newInstance(g);
		this.jvm = new JVMNode(g);
		this.byransha = new Byransha(g);
		this.os = new OSNode(g);
		this.admin = new User(g, g.systemUser, "admin", "admin"); // self accept
		this.guest = new User(g, g.systemUser, "user", "test");
		this.eventList = new EventListOneFilePerDay(g, new File(System.getProperty("user.home") + "/.byransha/events"));

	}

	@Override
	public String whatIsThis() {
		return "a node representing the system";
	}

	@Override
	public String prettyName() {
		return "system";
	}

}
