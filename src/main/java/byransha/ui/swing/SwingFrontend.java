package byransha.ui.swing;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import byransha.graph.BGraph;
import byransha.nodes.system.SystemNode;
import byransha.nodes.system.UIPreferences;

public class SwingFrontend extends SystemNode {

	public final UIPreferences ui;
	static JFrame f = new JFrame("Byransha");
	public final ByranshaUserPane sheet = new DocPane_TextPane();

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
		f.setSize(new Dimension(width, screenSize.height));
		f.setLocation((screenSize.width - width) / 2, 0);
		f.setContentPane(scroll);

		// setNode(new AuthenticateAction(g));
		currentUser().listeners.add(n -> sheet.addNode(n));
		currentUser().jumpTo(currentUser());
		f.setVisible(true);

		this.ui = new UIPreferences(g);
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
