package byransha.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import byransha.event.EventList;
import byransha.event.SingleFileEventList;
import byransha.graph.index.AllIndexes;
import byransha.graph.list.action.ListNode;
import byransha.graph.relection.ClassNode;
import byransha.lab.Genre.Female;
import byransha.lab.Genre.Male;
import byransha.lab.Genre.NotGenred;
import byransha.network.NetworkAgent;
import byransha.primitive.ValuedNode;
import byransha.system.Byransha;
import byransha.system.JVMNode;
import byransha.system.OperatingSystem;
import byransha.system.SystemNode;
import byransha.system.User;
import byransha.translate.GoogleTranslator;
import byransha.translate.Translator;
import byransha.ui.swing.SwingFrontend;
import io.github.classgraph.ClassGraph;

public class Hub extends SystemNode {
	@ShowInKishanView
	public final ListNode<ThreadNode> threads = new ListNode<ThreadNode>(this, "threads", ThreadNode.class);

	@ShowInKishanView
	private User currentUser = new User(this, "guest");

	public AllIndexes indexes = new AllIndexes(this);
	@ShowInKishanView
	public final AllIndexesNode indexesNode = new AllIndexesNode(this);

	@ShowInKishanView
	public BNode application;
	@ShowInKishanView
	public final JVMNode jvm = new JVMNode(this);
	@ShowInKishanView
	public final Byransha byransha = new Byransha(this);
	@ShowInKishanView
	public final OperatingSystem os = new OperatingSystem(this);
	@ShowInKishanView
	public final ErrorLog errorLog = new ErrorLog(this);
	@ShowInKishanView
	public final TinyChat tinyChat = new TinyChat(this);
	@ShowInKishanView
	public final EventList eventList = new SingleFileEventList(this,
			new File(System.getProperty("user.home"), "byransha-events.bin"));
	// public WebServer webServer;
	// public ByranshaWebSocketServer webSocketServer;

	@ShowInKishanView
	public SwingFrontend swingInterface;
	@ShowInKishanView
	public final NetworkAgent network;
	@ShowInKishanView
	public final Translator translator = new GoogleTranslator(this);
	// public final Authenticate auth = new LdapAuthenticator(this);

	public final List<CurrentUserListener> userSwitchingListeners = new ArrayList<>();


	class graph extends Category {
	}

	public Hub(int port) throws Exception {
		super(null);
//		 indexes.add(this);
		this.network = new NetworkAgent(this, port);
		network.start();
		new Male(this);
		new Female(this);
		new NotGenred(this);

		var visitor = new VisitorRole(this);
		var admin = new AdminRole(this);

		currentUser.roles.elements.add(admin);
	}

	@ShowInKishanView
	public int nbNodes() {
		return BNode.nbInstances;
	}

	@ActionMethod
	@AddButtonOnKishanView
	public void writeAllToDisk() {
		for (var n : indexes.byClass.getClassNodeFor(ValuedNode.class).allInstances().elements) {
			((ValuedNode) n).writeValueToDisk();
		}
	}

	@Override
	public final Hub hub() {
		return this;
	}

	public void setCurrentUser(User newUser) {
		if (newUser != currentUser) {
			this.currentUser = newUser;

			if (swingInterface != null) {
				userSwitchingListeners.forEach(l -> l.userSwitchedTo(currentUser, newUser));
			}
		}
	}

	public User getCurrentUser() {
		return currentUser;
	}

	@Override
	public void createActions() {
		super.createActions();
	}

	@Override
	public String whatIsThis() {
		return "a graph";
	}

	@Override
	public String toString() {
		return "hub";
	}

	public User currentUser() {
		return currentUser;
	}

	@ShowInKishanView
	public List<ClassNode> businessClasses() {
		return classesIn(application.getClass().getPackage(), BusinessNode.class);
	}

	public List<ClassNode> classesIn(Package p, Class superclass) {
		var r = new ArrayList<ClassNode>();

		for (var c : new ClassGraph().enableAllInfo().acceptPackages(p.getName()).scan().getAllClasses()
				.loadClasses()) {
			if (superclass.isAssignableFrom(c) && (c.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) == 0
					&& c.getDeclaringClass() == null) {
				try {
					var constr = c.getConstructor(BNode.class);

					if (constr != null) {
						r.add(hub().indexes.byClass.getClassNodeFor(c));
					}
				} catch (NoSuchMethodException err) {
					// Ignore classes without the required constructor
				}
			}
		}

		return r;
	}

}
