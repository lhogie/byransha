package byransha.ui.swing;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import byransha.graph.BGraph;
import byransha.graph.action.list.ListNode;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.SystemNode;
import byransha.nodes.system.User;
import byransha.util.ListChangeListener;

public class SwingFrontend extends SystemNode {

	public static JFrame f = new JFrame();
	public final Map<ChatNode, ChatPanel> sheets = new HashMap<>();

	public final ListNode<FontNode> fonts;
	private final JPanel chatsPanel;

	public SwingFrontend(BGraph g) {
		super(g);
		f.setTitle("Byransha v" + g.byransha.VERSION + " (contact: luc.hogie@cnrs.fr)");

		fonts = new ListNode<>(g, "available fonts");
		for (var font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
//			fonts.elements.add(new FontNode(g, font));
		}

		// setLookAndFeel("WebLaf");
		g.swing = this;

		this.chatsPanel = new JPanel(new GridLayout(1, 1));
		chatsPanel.setOpaque(true);
		chatsPanel.setBackground(g.ui.backgroundColor.get());

		FontUIResource customFont = new FontUIResource("ProximaNova-Medium", Font.PLAIN, 14);
		Utils.setUIFont(customFont);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		f.setSize(Utils.initialSize);
		f.setLocation(Utils.initialLocation);
		f.add(chatsPanel);
		f.setVisible(true);
		g.userSwitchingListeners.add((formerUser, newUser) -> considerUser(newUser));

		considerUser(g.currentUser());
		g.currentUser().chatList.get().forEach(chatNode -> addChatPanelFor(chatNode));

	}

	private void considerUser(User newUser) {
		chatsPanel.removeAll();
		var gl = (GridLayout) chatsPanel.getLayout();
		gl.setColumns(newUser.chatList.elements.size());
		newUser.chatList.elements.forEach(chat -> {
			System.out.println("dfsdfsfsd  " + newUser.chatList.elements.size());
			// addChatPanelFor(chat);
			System.out.println("dfsdfsfsd  " + newUser.chatList.elements.size());
		});
		newUser.chatList.elements.listeners.add(new ListChangeListener<ChatNode>() {
			@Override
			public void onAdd(ChatNode chatNode) {
				addChatPanelFor(chatNode);
			}

			@Override
			public void onRemove(ChatNode chat) {
				f.setSize(f.getSize().width - Utils.chatWidth, f.getSize().height);
				ChatPanel a = sheets.get(chat);
				chatsPanel.remove(a);
				gl.setColumns(gl.getColumns() - 1);
				chatsPanel.revalidate();
				chatsPanel.repaint();
			}
		});
	}

	private void addChatPanelFor(ChatNode chatNode) {
		var chatPanel = new ChatPanel(chatNode);
		sheets.put(chatNode, chatPanel);
		var gl = (GridLayout) chatsPanel.getLayout();
		gl.setColumns(gl.getColumns() + 1);
		chatsPanel.add(chatPanel);
		chatsPanel.revalidate();
		chatsPanel.repaint();
		f.setSize(Math.min(f.getSize().width, Utils.screenSize.width) + Utils.chatWidth, f.getSize().height);

		if (f.getSize().width > Utils.screenSize.width) {
			f.setSize(Utils.screenSize.width, f.getSize().height);
		}

		if (f.getLocation().x + f.getSize().width > Utils.screenSize.width) {
			int tooMuch = f.getLocation().x + f.getSize().width - Utils.screenSize.width;
			f.setLocation(f.getLocation().x - tooMuch, f.getLocation().y);
		}
	}

	private static void setLookAndFeel(String name) {
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");
		// https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes#how-to-use
		System.setProperty("flatlaf.useNativeFont", "false");
		com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme.setup();
	}

	@Override
	public String whatIsThis() {
		return "the Swing GUI";
	}

	@Override
	public String prettyName() {
		return "Swing GUI";
	}
}
