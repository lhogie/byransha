package byransha;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.SystemB;
import byransha.nodes.system.UIPreferences;

public class SwingFrontend extends SystemB {

	public final UIPreferences ui;
	private JPanel viewsPanel;

	public SwingFrontend(BBGraph g) {
		super(g);
		setLookAndFeel("WebLaf");
		viewsPanel = new JPanel(new GridBagLayout());
		g.systemNode.swing = this;
		var f = new JFrame("Byransha");
		g.systemNode.changeUserListener.add(u -> setNode(u));
		f.setContentPane(new JScrollPane(viewsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		var width = (9 * screenSize.height) / 16;
		f.setSize(new Dimension(width, screenSize.height));
		f.setLocation((screenSize.width - width) / 2, 0);
		f.setVisible(true);

		// setNode(new AuthenticateAction(g));
		setNode(g.systemNode);

		this.ui = new UIPreferences(g);
	}

	private void setLookAndFeel(String name) {
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");
		// https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes#how-to-use
		System.setProperty("flatlaf.useNativeFont", "false");
		com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme.setup();
	}

	public void setNode(BNode n) {
		viewsPanel.removeAll();
		int y = 0;

		for (var v : n.views()) {
			if (v.showInViewList()) {
				var c = v.createComponent(n);
				c.setBorder(new TitledBorder(v.whatItShows()));

				GridBagConstraints gbc = new GridBagConstraints();
				gbc.insets = new Insets(10,10, 10, 10);
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.gridy = y++;
				viewsPanel.add(c, gbc);
			}
		}

		viewsPanel.invalidate();
		viewsPanel.validate();
		viewsPanel.doLayout();
		viewsPanel.repaint();
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
