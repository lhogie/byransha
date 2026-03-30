package byransha.ui.javafx;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.view.ErrorsView;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.SystemNode;
import byransha.util.ListChangeListener;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class JavaFXFrontend extends SystemNode {

	private final VBox vbox;

	public JavaFXFrontend(BGraph g, VBox vbox) {
		super(g);
//		g.javafx = this;
		this.vbox = vbox;

		ChatNode chat = null;
		g.userSwitchingListeners.add((old, newUser) -> addNode(newUser));
		chat.nodes.elements.listeners.add(new ListChangeListener<BNode>() {

			@Override
			public void onRemove(BNode n) {

			}

			@Override
			public void onAdd(BNode n) {
				addNode(n);
			}
		});
		chat.append(g);
	}

	public void addNode(BNode n) {
		vbox.getChildren().clear();
		vbox.getChildren()
				.add(new ByText("'" + n + "'is " + n.whatIsThis() + ". Its ID is " + n.id() + ".\n\n"));
		n.views().getFirst().writeTo(vbox);
		vbox.getChildren().add(new Text("\nErrors:\n"));
		n.findView(ErrorsView.class).writeTo(vbox);
		vbox.getChildren().add(new Text("\nWhat do you want to do?"));
//		n.actions().forEach(a -> vbox.getChildren().add( a.createJumpButton(this)));
		vbox.getChildren().add(new Text("\n"));
	}

	@Override
	public String whatIsThis() {
		return "the JavaFX GUI";
	}

	@Override
	public String toString() {
		return "JavaFX GUI";
	}

}
