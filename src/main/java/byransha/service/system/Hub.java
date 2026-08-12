package byransha.service.system;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import byransha.Element;
import byransha.access_control.AdminRole;
import byransha.access_control.CurrentUserListener;
import byransha.access_control.User;
import byransha.access_control.VisitorRole;
import byransha.action.ActionMethod;
import byransha.action.AddButtonOnKishanView;
import byransha.action.Category;
import byransha.action.base.ShowInKishanView;
import byransha.event.EventList;
import byransha.event.SingleFileEventList;
import byransha.graph.relection.ClassNode;
import byransha.index.AllIndexes;
import byransha.index.AllIndexesElement;
import byransha.lab.LabApplication;
import byransha.lab.LabElement;
import byransha.list.action.ListNode;
import byransha.network.Network;
import byransha.primitive.ValuedElement;
import byransha.service.misc.TinyChat;
import byransha.thread.ThreadNode;
import byransha.translate.GoogleTranslator;
import byransha.translate.Translator;
import byransha.ui.swing.SwingFrontend;
import io.github.classgraph.ClassGraph;

public class Hub extends Element {
	@ShowInKishanView
	public final ListNode<ThreadNode> threads = new ListNode<ThreadNode>(this, null, "threads", ThreadNode.class);

	@ShowInKishanView
	private User currentUser = new User(this, "guest");

	public AllIndexes indexes = new AllIndexes(this);
	@ShowInKishanView
	public final AllIndexesElement indexesNode = new AllIndexesElement(this);

	@ShowInKishanView
	public Element application = new LabApplication(this);
	@ShowInKishanView
	public final JVM jvm = new JVM(this);
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

	@ShowInKishanView
	public SwingFrontend swingInterface;
	@ShowInKishanView
	public final Network network;
	@ShowInKishanView
	public final Translator translator = new GoogleTranslator(this);

	public final List<CurrentUserListener> userSwitchingListeners = new ArrayList<>();

	class graph extends Category {
	}

	public Hub(int port) throws Exception {
		super(null, null);
//		 indexes.add(this);
		this.network = new Network(this, port);
		network.start();

		var visitor = new VisitorRole(this);
		var admin = new AdminRole(this);

		currentUser.roles.elements.add(admin);
	}

	@ShowInKishanView
	public int nbElements() {
		return Element.nbInstances;
	}

	@ActionMethod
	@AddButtonOnKishanView
	public void writeAllValuesToDisk() {
		for (var n : indexes.byClass.getClassNodeFor(ValuedElement.class).allInstances().elements) {
			((ValuedElement) n).writeValueToDisk();
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
		return "the hub for all elements (root of the element tree)";
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
		return classesIn(application.getClass().getPackage(), LabElement.class);
	}

	public List<ClassNode> classesIn(Package p, Class superclass) {
		var r = new ArrayList<ClassNode>();

		for (var c : new ClassGraph().enableAllInfo().acceptPackages(p.getName()).scan().getAllClasses()
				.loadClasses()) {
			if (superclass.isAssignableFrom(c) && (c.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) == 0
					&& c.getDeclaringClass() == null) {
				try {
					var constr = c.getConstructor(Element.class);

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
