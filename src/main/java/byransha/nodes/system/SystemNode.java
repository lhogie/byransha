package byransha.nodes.system;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import byransha.event.EventList;
import byransha.event.InMemoryEventList;
import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.ErrorLog;
import byransha.swing.SwingFrontend;
import byransha.web.ByranshaWebSocketServer;
import byransha.web.WebServer;

public class SystemNode extends SystemB {
	public final User admin, guest;
	public BNode application;
	public final JVMNode jvm;
	public final Byransha byransha;
	public final OSNode os;
	public final ErrorLog errorLog;
	public final EventList eventList;
	public WebServer webServer;
	public ByranshaWebSocketServer webSocketServer;
	public SwingFrontend swing;
	private User currentUser;

	public SystemNode(BBGraph g, File directory) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super(g);
		this.jvm = new JVMNode(g);
		this.byransha = new Byransha(g);
		this.os = new OSNode(g);
		this.errorLog = new ErrorLog(g);
		this.admin = new User(g, "admin", "admin".hashCode()); // self accept
		this.guest = new User(g, "user", "test".hashCode());
		this.eventList = new InMemoryEventList(g);
		setCurrentUser(guest);
	}

	@Override
	public String whatIsThis() {
		return "a node representing the system";
	}

	@Override
	public String prettyName() {
		return "system";
	}

	public static interface CurrentUserListener {
		void changed(User u);
	}

	public final List<CurrentUserListener> changeUserListener = new ArrayList<>();

	public void setCurrentUser(User newUser) {
		if (newUser != currentUser) {
			this.currentUser = newUser;
			changeUserListener.forEach(l -> l.changed(newUser));
		}

	}

	public User getCurrentUser() {
		return currentUser;
	}

}
