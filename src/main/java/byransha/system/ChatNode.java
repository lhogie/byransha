package byransha.system;

import java.awt.Window;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.ai.QueryIA;
import byransha.graph.Action;
import byransha.graph.ActionMethod;
import byransha.graph.BNode;
import byransha.graph.ProcedureAction;
import byransha.graph.ShowInKishanView;
import byransha.graph.action.JumpToAnotherNode;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.primitive.StringNode;
import byransha.util.ByUtils;

public class ChatNode extends BNode {
	@ShowInKishanView
	public ListNode<BNode> nodes = new ListNode<>(this, "history", BNode.class);
	final User user;
	private static volatile Boolean AlerteIA = false;
	public static volatile boolean NodeAIUsed = false;

	public ChatNode(User user) {
		super(user);
		this.user = user;
		user.chats.elements.add(this);
	}

	public BNode currentNode() {
		return nodes.get().isEmpty() ? null : nodes.get().getLast();
	}

	public void append(BNode n) {
		Objects.requireNonNull(n, "cannot append null node to chat");
		System.out.println("appending " + n + " to chat " + this);
		if (n instanceof QueryIA) {
                    try {
                        if (!(InetAddress.getLocalHost().getHostName().equals(System.getenv("PUBLIC_SERVER_NAME")))) {
							if (AlerteIA == false) {
							afficherAlerteOllama();
							// afficherPublicKey();
						}
						AlerteIA = true;
						NodeAIUsed = true;
						if (NodeAIUsed) {
							if (afficherChargementOllama()) {
								QueryIA.startOllama();
							}
						}
                        }
						else {
							QueryIA.startOllama();
			
						}
							
                    } catch (UnknownHostException ex) {
                        System.getLogger(ChatNode.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }
					
			 
		}

		if (!nodes.elements.isEmpty() && n == nodes.elements.getLast()) // if same node
			return;

		if (n instanceof Action action) {
			if (action.parameters().isEmpty()) {
				action.outputConsumer = feedback -> append(new StringNode(null, (String) feedback, ".*"));
				action.chat = this;
				action.execSync();

				if (action instanceof FunctionAction fa) {
					append(fa.result);
				}
			} else {
				nodes.elements.add(action);
				action.chat = this;
			}
		} 
		else {
			nodes.elements.add(n);
		}
	}
	private void afficherAlerteOllama() {
		Timer t = new Timer(10000, e -> {
			Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    if (window instanceof JDialog) {
                        JDialog dialog = (JDialog) window;
                        if (dialog.getContentPane().getComponentCount() == 1
                            && dialog.getContentPane().getComponent(0) instanceof JOptionPane){
                            dialog.dispose();
                        }
                    }
				}
		});
		
		t.setRepeats(false);
		t.start();
        JOptionPane.showMessageDialog(
            null, 
           "L'utilisation de l'IA sans serveur distant requiert l'installation d'Ollama ainsi que du modèle sur votre machine locale.", 
            "Configuration requise", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
	private boolean afficherChargementOllama() {
		// boite de dialogue avec oui/non et message de chargement si on clique sur non + timer de 10 secondes pour fermer la boite de dialogue sinon c'est non par default
		Timer t = new Timer(10000, e -> {
			Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    if (window instanceof JDialog) {
                        JDialog dialog = (JDialog) window;
                        if (dialog.getContentPane().getComponentCount() == 1
                            && dialog.getContentPane().getComponent(0) instanceof JOptionPane){
                            dialog.dispose();
                        }
                    }
				}
		});
		
		t.setRepeats(false);
		t.start();
		System.out.println("Affichage de la boîte de dialogue pour le chargement de l'IA...");
		int result = JOptionPane.showConfirmDialog(
			null, 
			"Voulez vous charger l'IA sur votre machine locale ?\n\nNote : Le chargement peut prendre un certain temps selon la puissance de votre machine.\n\nNote2: pré-charger l'IA permet de reduire le temps de réponse de l'IA lors de la première requête.", 
			"Chargement de l'IA", 
			JOptionPane.YES_NO_OPTION, 
			JOptionPane.INFORMATION_MESSAGE
		);

		if (result == JOptionPane.YES_OPTION) {
			// L'utilisateur a cliqué sur "Oui"
			System.out.println("L'utilisateur a accepté le chargement de l'IA.");
			return true;
		}
		System.out.println("L'utilisateur a refusé le chargement de l'IA.");
		JOptionPane.showMessageDialog(
			null, 
			"Le pré-chargement de l'IA a été refusé..", 
			"pré-chargement de l'IA refusé", 
			JOptionPane.WARNING_MESSAGE
		);
		return false;
	}
		
		

	@Override
	public void createActions() {
		cachedActions.elements.add(new Export(this));
		cachedActions.elements.add(new JumpToAnotherNode(this));
		super.createActions();
	}

	ArrayNode export() {
		ArrayNode r = new ArrayNode(ByUtils.factory);

		for (var n : nodes.elements) {
			var on = new ObjectNode(ByUtils.factory);
			r.add(on);
			on.put("id", n.id());
			on.put("toString", n.toString());

			if (n instanceof ProcedureAction action) {
				var parmNode = new ObjectNode(ByUtils.factory);
				on.set("parameters", parmNode);

				n.forEachOutInFields(n.getClass(), ProcedureAction.class,
						(f, o, ro) -> parmNode.put(f.getName(), o.toString()));
			}
		}

		return r;
	}

	@Override
	public String whatIsThis() {
		return "a chat";
	}

	@Override
	public String toString() {
		return user + "'s chat";
	}

	@ActionMethod
	public void showSuperNode() {
		append(hub());
	}
}
