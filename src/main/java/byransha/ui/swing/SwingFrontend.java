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

import byransha.Chat;
import byransha.Service;
import byransha.access_control.User;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;
import byransha.network.Message;
import byransha.primitive.ColorNode;
import byransha.primitive.LongNode;
import byransha.service.system.Byransha;
import byransha.service.system.Hub;
import byransha.ui.ColorSchemeNode;
import byransha.util.ByUtils;

public class SwingFrontend extends Service {
	@ShowInKishanView
	public final ColorSchemeNode colorStyle = List.of(ColorPalette.Style.values()).stream()
			.map(s -> new ColorSchemeNode(this, s)).toList().getFirst();
	@ShowInKishanView
	public final LongNode transparencyForNodeBackground = new LongNode(this, null, 20);
	public ColorNode backgroundColor = new ColorNode(this, null, colorStyle.get()[0]);

	@ShowInKishanView
	public final ListNode<FontNode> fonts = new ListNode<>(this, null, "available fonts", FontNode.class);
	public JFrame frame;

	public SwingFrontend(Hub g) {
		super(g);

		for (var font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
			// fonts.elements.add(new FontNode(g, font));
		}

		g.swingInterface = this;
		g.userSwitchingListeners.add((formerUser, newUser) -> considerUser(newUser));

		try {
			this.frame = new JFrame();
			frame.setTitle("Byransha v" + hub().byransha.VERSION + " (contact: luc.hogie@cnrs.fr)");

			if (positionAndSizeFile.exists()) {
				var bytes = Files.readAllBytes(positionAndSizeFile.toPath());
				PositionAndSize ps = (PositionAndSize) ByUtils.serializer.fromBytes(bytes);
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
						Files.write(positionAndSizeFile.toPath(), ByUtils.serializer.toBytes(ps));
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
			hub().errorLog.add(e, false);
		}
	}

	static File positionAndSizeFile = new File(Byransha.homeDirectory, "window_size_and_position.ser");

	private void considerUser(User newUser) {
		if (frame != null) {
			frame.getContentPane().removeAll();
		}

		if (newUser.chats.elements.isEmpty()) {
			new Chat(newUser).nodes.elements.add(hub());
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

	@Override
	protected void incomingMessage(Message msg) {
		// TODO Auto-generated method stub

	}
}
