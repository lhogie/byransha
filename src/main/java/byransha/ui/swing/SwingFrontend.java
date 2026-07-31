package byransha.ui.swing;

import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import byransha.graph.BGraph;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.network.NetworkAgent;
import byransha.nodes.primitive.ColorNode;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.system.Byransha;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.SystemNode;
import byransha.nodes.system.User;
import byransha.ui.ColorSchemeNode;

public class SwingFrontend extends SystemNode {
	@ShowInKishanView
	public final ColorSchemeNode colorStyle = List.of(ColorPalette.Style.values()).stream()
			.map(s -> new ColorSchemeNode(this, s)).toList().getFirst();
	@ShowInKishanView
	public final LongNode transparencyForNodeBackground = new LongNode(this, 20);
	public ColorNode backgroundColor = new ColorNode(this, colorStyle.get()[0]);

	@ShowInKishanView
	public final ListNode<FontNode> fonts = new ListNode<>(this, "available fonts", FontNode.class);
	public JFrame frame;

	public SwingFrontend(BGraph g) {
		super(g);

		for (var font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
			// fonts.elements.add(new FontNode(g, font));
		}

		g.swing = this;
		g.userSwitchingListeners.add((formerUser, newUser) -> considerUser(newUser));

		try {
			this.frame = new JFrame();
			frame.setTitle("Byransha v" + g().byransha.VERSION + " (contact: luc.hogie@cnrs.fr)");

			if (positionAndSizeFile.exists()) {
				var bytes = Files.readAllBytes(positionAndSizeFile.toPath());
				PositionAndSize ps = (PositionAndSize) NetworkAgent.serializer.fromBytes(bytes);
				frame.setLocation(ps.location());
				frame.setSize(ps.size());
			} else {
				frame.setLocation(0, 0);
				frame.setSize(9 * Utils.screenSize.height / 16, Utils.screenSize.height);
			}

			frame.setVisible(true);
			// considerUser(g.currentUser());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					System.exit(46);
				}
			});

			// Add the ComponentListener using ComponentAdapter
			frame.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentMoved(ComponentEvent e) {
					saveLocationAndSize();
				}

				private void saveLocationAndSize() {
					var ps = new PositionAndSize(frame.getSize(), frame.getLocation());
					try {
						Files.write(positionAndSizeFile.toPath(), NetworkAgent.serializer.toBytes(ps));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void componentResized(ComponentEvent e) {
					saveLocationAndSize();
				}
			});
		} catch (Exception e) {
			g().errorLog.add(e, false);
		}
	}

	static File positionAndSizeFile = new File(Byransha.homeDirectory, "window_size_and_position.ser");

	private void considerUser(User newUser) {
		if (frame != null) {
			frame.getContentPane().removeAll();
		}

		if (newUser.chats.elements.isEmpty()) {
			new ChatNode(newUser).nodes.elements.add(g());
		}

		var panelList = newUser.chats.elements.stream().map(c -> new ChatPanel(c)).toList();
		var p = new JPanel(new GridLayout(1, panelList.size()));
		System.out.println(newUser + "  " + panelList.size());
		panelList.forEach(p::add);
		frame.setContentPane(p);
		frame.doLayout();
		frame.revalidate();
		frame.repaint();
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
