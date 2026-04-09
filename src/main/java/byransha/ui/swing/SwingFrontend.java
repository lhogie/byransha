package byransha.ui.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import byransha.graph.BGraph;
import byransha.graph.action.list.ListNode;
import byransha.nodes.primitive.ColorNode;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.SystemNode;
import byransha.nodes.system.User;
import byransha.ui.ColorSchemeNode;
import byransha.util.ListenableList;

public class SwingFrontend extends SystemNode {
	public final ColorSchemeNode colorStyle;
	public final LongNode transparencyForNodeBackground = new LongNode(this, 5);
	public ColorNode backgroundColor = new ColorNode(this, Color.pink);

	public final Map<ChatNode, JFrame> frames = new HashMap<>();

	public final ListNode<FontNode> fonts = new ListNode<>(g, "available fonts");

	public SwingFrontend(BGraph g) {
		super(g);
		
		var schemeNodes = List.of(ColorPalette.Style.values()).stream().map(s -> new ColorSchemeNode(g, s)).toList();
		this.colorStyle = schemeNodes.getFirst();

		for (var font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
			fonts.elements.add(new FontNode(g, font));
		}

		g.swing = this;

		FontUIResource customFont = new FontUIResource("ProximaNova-Medium", Font.PLAIN, 14);
		Utils.setUIFont(customFont);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		g.userSwitchingListeners.add((formerUser, newUser) -> considerUser(newUser));

		considerUser(g.currentUser());
	}

	private void considerUser(User newUser) {
		newUser.chatList.elements.forEach(chat -> addChatPanelFor(chat));

		newUser.chatList.elements.addListener(new ListenableList.Listener<ChatNode>() {

			@Override
			public void onAdded(int index, ChatNode chatNode) {
				addChatPanelFor(chatNode);
			}

			@Override
			public void onRemoved(int index, ChatNode chat) {
				var frame = frames.remove(chat);
				frame.dispose();
			}

			@Override
			public void onSet(int index, ChatNode oldElement, ChatNode newElement) {
				throw new UnsupportedOperationException("not implemented yet");
			}
		});
	}

	private void addChatPanelFor(ChatNode chatNode) {
		var ref = frames.isEmpty() ? null : frames.values().iterator().next();
		var f = new JFrame();
		f.setTitle("Byransha v" + g.byransha.VERSION + " (contact: luc.hogie@cnrs.fr)");

		if (ref == null) {
			f.setSize(Utils.initialSize);
			f.setLocation(Utils.initialLocation);
		} else {
			f.setSize(ref.getSize());
			var location = ref.getLocation();
			location.x += ref.getSize().width;
			f.setLocation(location);
		}

		var chatPanel = new ChatPanel(chatNode);
		f.setContentPane(chatPanel);
		frames.put(chatNode, f);
		f.setSize(600, 800);
		f.setVisible(true);
	}

	@Override
	public String whatIsThis() {
		return "the Swing GUI";
	}

	@Override
	public String toString() {
		return "Swing GUI";
	}
}
