package byransha.nodes.system;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import byransha.event.EventList;
import byransha.event.EventListOneBigFile;
import byransha.graph.BBGraph;
import byransha.graph.ErrorLog;
import byransha.swing.SwingFrontend;
import byransha.web.WebServer;

public class SystemNode extends SystemB {
	public final User admin, guest;
	public final UserApplication application;
	public final JVMNode jvm;
	public final Byransha byransha;
	public final OSNode os;
	public final ErrorLog errorLog;
	public final EventList eventList;
	public WebServer webServer;
	public SwingFrontend swing;
	private User currentUser;

	public SystemNode(BBGraph g, Class<? extends UserApplication> appClass, File directory)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		super(g);
		this.application = appClass.getConstructor(BBGraph.class, User.class).newInstance(g, g.systemUser);
		this.jvm = new JVMNode(g);
		this.byransha = new Byransha(g);
		this.os = new OSNode(g);
		this.errorLog = new ErrorLog(g);
		this.admin = new User(g, g.systemUser, "admin", "admin"); // self accept
		this.guest = new User(g, g.systemUser, "user", "test");
		this.eventList = new EventListOneBigFile(g, directory);
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

	public final List<CurrentUserListener> userListener = new ArrayList<>();

	public void setCurrentUser(User newUser) {
		if (newUser != currentUser) {
			this.currentUser = newUser;
			userListener.forEach(l -> l.changed(newUser));
		}

	}

	public User getCurrentUser() {
		return currentUser;
	}

}
