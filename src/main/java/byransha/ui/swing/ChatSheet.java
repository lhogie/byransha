package byransha.ui.swing;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BoxLayout;

import byransha.graph.BNode;
import byransha.graph.view.ErrorsView;
import byransha.nodes.system.ChatNode;
import byransha.util.ListChangeListener;

public class ChatSheet extends Sheet {

	public final ChatNode chat;

	public ChatSheet(ChatNode chat) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.chat = chat;
		setOpaque(false);
		chat.nodes.elements.forEach(node -> appendNode(node));
		chat.nodes.elements.listeners.add(new ListChangeListener<BNode>() {

			@Override
			public void onAdd(BNode n) {
				appendNode(n);
			}

			@Override
			public void onRemove(BNode n) {
				removeNode(n);
			}
		});

		Utils.IdDropTarget(chat.g, this, n -> chat.nodes.elements.add(n));
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 20;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 60;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true; // match viewport width so WrapPanels reflow correctly
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false; // allow vertical scrolling
	}

	void appendNode(BNode n) {
		this.bgColor = n.getBackgroundColor();

		newLine();
		appendToCurrentLine(Utils.idShower(n, 20, 0, chat));
		appendToCurrentLine(n + " (" + n.whatIsThis() + ")");
		newLine();
		newLine();
		n.views().getFirst().writeTo(this);
		newLine();

		var err = n.findView(ErrorsView.class);

		if (err.showInViewList()) {
			err.writeTo(this);
			newLine();
		}

		newLine();
		newLine();
		end();

		new Thread(() -> swipeDownInOneSecond()).start();
	}

	public void appendToCurrentLine(String s) {
		super.appendToCurrentLine(s, chat.g.translator);
	}


}
