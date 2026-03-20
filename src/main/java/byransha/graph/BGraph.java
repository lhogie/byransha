package byransha.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import byransha.event.EventList;
import byransha.event.InMemoryEventList;
import byransha.graph.action.Authenticate;
import byransha.graph.action.NewNodeCreator;
import byransha.graph.index.AllIndexes;
import byransha.network.NetworkAgent;
import byransha.nodes.lab.Genre.Female;
import byransha.nodes.lab.Genre.Male;
import byransha.nodes.lab.Genre.NotGenred;
import byransha.nodes.system.Argon;
import byransha.nodes.system.Byransha;
import byransha.nodes.system.JVMNode;
import byransha.nodes.system.OSNode;
import byransha.nodes.system.UIPreferences;
import byransha.nodes.system.User;
import byransha.translate.GoogleTranslator;
import byransha.translate.Translator;
import byransha.ui.javafx.JavaFXFrontend;
import byransha.ui.swing.SwingFrontend;

public class BGraph extends BNode {
	public final List<GraphListener> listeners = new ArrayList<GraphListener>();
	public final List<CurrentUserListener> userSwitchingListeners = new ArrayList<>();

	public AllIndexes indexes = new AllIndexes();
	public final AllIndexesNode inode = new AllIndexesNode(this);
	public final User admin = new User(this, "admin", Argon.hash("admin"));
	public final User guest = new User(this, "guest", Argon.hash(""));
	public BNode application;
	public final JVMNode jvm = new JVMNode(this);
	public final Byransha byransha = new Byransha(this);
	public final OSNode os = new OSNode(this);
	public final ErrorLog errorLog = new ErrorLog(this);
	public final EventList eventList = new InMemoryEventList(this);
	public final NewNodeCreator nodeCreator = new NewNodeCreator(this);
	public final Authenticate authenticator = new Authenticate(this, this);
//	public WebServer webServer;
//	public ByranshaWebSocketServer webSocketServer;
	public SwingFrontend swing;
	private User currentUser = guest;
	public JavaFXFrontend javafx;
	public final UIPreferences ui = new UIPreferences(this);
	public final NetworkAgent networkAgent = new NetworkAgent(this);
	public final Translator translator = new GoogleTranslator(this);

	public BGraph(File directory) throws Exception {
		super(null);
		indexes.add(this);

		new Male(g);
		new Female(g);
		new NotGenred(g);
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new AllNodes(g));
		super.createActions();
	}

	public void setCurrentUser(User newUser) {
		if (newUser != currentUser) {
			this.currentUser = newUser;
			userSwitchingListeners.forEach(l -> l.userSwitchedTo(currentUser, newUser));
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
