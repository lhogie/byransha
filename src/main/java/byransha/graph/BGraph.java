package byransha.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import byransha.event.EventList;
import byransha.event.InMemoryEventList;
import byransha.graph.index.AllIndexes;
import byransha.network.NetworkAgent;
import byransha.nodes.lab.Genre.Female;
import byransha.nodes.lab.Genre.Male;
import byransha.nodes.lab.Genre.NotGenred;
import byransha.nodes.system.Byransha;
import byransha.nodes.system.JVMNode;
import byransha.nodes.system.OSNode;
import byransha.nodes.system.User;
import byransha.security.AuthAction;
import byransha.security.Authenticator;
import byransha.security.LdapAuthenticator;
import byransha.translate.GoogleTranslator;
import byransha.translate.Translator;
import byransha.ui.swing.SwingFrontend;

public class BGraph extends BNode {
	public AllIndexes indexes = new AllIndexes(this);
	public final AllIndexesNode indexesNode = new AllIndexesNode(this);

	public Authenticator authenticator = new LdapAuthenticator(null, null, null, readOnly);
	public AuthAction authenticatorNode = new AuthAction(g);

	public BNode application;
	public final JVMNode jvm = new JVMNode(this);
	public final Byransha byransha = new Byransha(this);
	public final OSNode os = new OSNode(this);
	public final ErrorLog errorLog = new ErrorLog(this);
	public final EventList eventList = new InMemoryEventList(this);
//	public WebServer webServer;
//	public ByranshaWebSocketServer webSocketServer;
	public SwingFrontend swing;
	public final NetworkAgent networkAgent = new NetworkAgent(this);
	public final Translator translator = new GoogleTranslator(this);
//	public final Authenticate auth = new LdapAuthenticator(this);
	public final List<CurrentUserListener> userSwitchingListeners = new ArrayList<>();

	class graph extends Category {
	}

	public BGraph(File directory) throws Exception {
		super(null);
		indexes.add(this);

		new Male(g);
		new Female(g);
		new NotGenred(g);
	}

	public User currentUser;

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
	public void createActions() {
		cachedActions.elements.add(new AllNodes(g));
		super.createActions();
	}

	@Override
	public String whatIsThis() {
		return "a graph";
	}

	@Override
	public String toString() {
		return "super node";
	}

}
