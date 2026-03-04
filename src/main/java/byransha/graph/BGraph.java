package byransha.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import byransha.event.EventList;
import byransha.event.InMemoryEventList;
import byransha.graph.action.NewNodeCreator;
import byransha.graph.index.AllIndexes;
import byransha.nodes.system.Argon;
import byransha.nodes.system.Byransha;
import byransha.nodes.system.JVMNode;
import byransha.nodes.system.OSNode;
import byransha.nodes.system.User;
import byransha.swing.SwingFrontend;
import byransha.web.ByranshaWebSocketServer;
import byransha.web.WebServer;

public class BGraph extends BNode {
	public final List<GraphListener> listeners = new ArrayList<GraphListener>();
	public final List<CurrentUserListener> changeUserListener = new ArrayList<>();

	public final AllIndexes i = new AllIndexes(this);
	public final User admin = new User(this, "admin", Argon.hash("admin"));
	public final User guest = new User(this, "user", Argon.hash("test"));
	public BNode application;
	public final JVMNode jvm = new JVMNode(this);
	public final Byransha byransha = new Byransha(this);
	public final OSNode os = new OSNode(this);
	public final ErrorLog errorLog = new ErrorLog(this);
	public final EventList eventList = new InMemoryEventList(this);
	public final NewNodeCreator nodeCreator = new NewNodeCreator(this);
	public WebServer webServer;
	public ByranshaWebSocketServer webSocketServer;
	public SwingFrontend swing;
	private User currentUser;

	public static interface CurrentUserListener {
		void changed(User u);
	}

	public BGraph(File directory) throws Exception {
		super(null);
		setCurrentUser(guest);
	}

	public void setCurrentUser(User newUser) {
		if (newUser != currentUser) {
			this.currentUser = newUser;
			changeUserListener.forEach(l -> l.changed(newUser));
		}
	}

	public User getCurrentUser() {
		return currentUser;
	}

	@Override
	public String whatIsThis() {
		return "a graph";
	}

	@Override
	public String prettyName() {
		return "super node";
	}

}
