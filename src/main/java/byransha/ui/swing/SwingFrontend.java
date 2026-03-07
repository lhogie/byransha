package byransha.ui.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import byransha.graph.BGraph;
import byransha.nodes.system.SystemNode;

public class SwingFrontend extends SystemNode {

	static JFrame f = new JFrame("Byransha");
	public final DocPane_JPanelFlowlaout sheet = new DocPane_JPanelFlowlaout();

	public SwingFrontend(BGraph g) {
		super(g);
		setLookAndFeel("WebLaf");
		g.swing = this;
		g.changeUserListener.add(u -> sheet.addNode(u));

		JScrollPane scroll = new JScrollPane(sheet.getComponent());
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		var width = (9 * screenSize.height) / 16;
		var size = new Dimension(width, screenSize.height);
		var location = new Point((screenSize.width - width) / 2, 0);
		f.setSize(size);
		f.setLocation(location);
		f.add(scroll);
		f.setLocationRelativeTo(null);

		// setNode(new AuthenticateAction(g));
		currentUser().jumpListeners.add(n -> sheet.addNode(n));
		currentUser().jumpTo(currentUser());
		f.setVisible(true);
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
