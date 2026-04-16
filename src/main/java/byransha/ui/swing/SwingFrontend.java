package byransha.ui.swing;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import byransha.graph.BGraph;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.ColorNode;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.SystemNode;
import byransha.nodes.system.User;
import byransha.ui.ColorSchemeNode;
import byransha.util.ListenableList;

public class SwingFrontend extends SystemNode {
	@ShowInKishanView
	public final ColorSchemeNode colorStyle = List.of(ColorPalette.Style.values()).stream()
			.map(s -> new ColorSchemeNode(g, s)).toList().getFirst();
	@ShowInKishanView
	public final LongNode transparencyForNodeBackground = new LongNode(this, 5);
	public ColorNode backgroundColor = new ColorNode(this, colorStyle.get()[0]);

	@ShowInKishanView
	public final ListNode<FontNode> fonts = new ListNode<>(g, "available fonts", FontNode.class);
	public final JFrame f;

	public SwingFrontend(BGraph g) {
		super(g);

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


		this.f = new JFrame();
		f.setTitle("Byransha v" + g.byransha.VERSION + " (contact: luc.hogie@cnrs.fr)");

		f.setSize(Utils.initialSize);
		f.setLocation(0, 0);

		f.setSize(9 * Utils.screenSize.height / 16, Utils.screenSize.height);
		f.setVisible(true);
		considerUser(g.currentUser());
	}

	private void considerUser(User newUser) {
		f.getContentPane().removeAll();
		var panelList = newUser.chatList.elements.stream().map(c -> new ChatPanel(c)).toList();
		var p = new JPanel(new GridLayout(1, panelList.size()));
		panelList.forEach(p::add);
		f.setContentPane(p);
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
