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
import byransha.nodes.system.ChatListener;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.SystemNode;
import byransha.nodes.system.User;

public class SwingFrontend extends SystemNode {

	static JFrame f = new JFrame();
	public final Map<ChatNode, ChatSheet> sheets = new HashMap<>();

	public ListNode<FontNode> fonts;

	public SwingFrontend(BGraph g) {
		super(g);
		f.setTitle("Byransha v" + g.byransha.VERSION +  " (contact: luc.hogie@cnrs.fr)");

		fonts = new ListNode<>(g, "available fonts");
		for (var font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
			fonts.add(new FontNode(g, font));
		}

	//	setLookAndFeel("WebLaf");
		g.swing = this;

		var gl = new GridLayout(1, 1);
		var chatsPanel = new JPanel(gl);

		g.userSwitchingListeners.add((formerUser, newUser) -> {
			System.out.println("NE> user " + newUser);
			newUser.chatListeners.add(new ChatListener() {
				@Override
				public void newChat(User user, ChatNode chat) {
					System.out.println("NEW CHAT " + chat);
					f.setSize(Math.min(f.getSize().width,Utils.screenSize.width) + Utils.chatWidth, f.getSize().height);
					var sheet = new ChatSheet(chat);
					chat.newNodeListeners.add(n -> sheet.addNode(n));
					sheets.put(chat, sheet);
					gl.setColumns(gl.getColumns() + 1);
					chatsPanel.add(sheet.scroll);
					chatsPanel.revalidate();
					chatsPanel.repaint();
					chat.add(user);
				}

				@Override
				public void chatClosed(User user, ChatNode chat) {
					f.setSize(f.getSize().width - Utils.chatWidth, f.getSize().height);
					chatsPanel.remove(sheets.get(chat));
					gl.setColumns(gl.getColumns() - 1);
					chatsPanel.revalidate();
					chatsPanel.repaint();
				}
			});
		});

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

		g.setCurrentUser(g.guest);
		new ChatNode(g.guest, g.guest);
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
