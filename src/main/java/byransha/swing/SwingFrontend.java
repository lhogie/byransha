package byransha.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.view.AvailableActionsView;
import byransha.graph.view.AvailableViewsView;
import byransha.graph.view.NodeView;
import byransha.nodes.system.SystemB;
import byransha.nodes.system.UIPreferences;
import byransha.swing.MyLayout.Direction;

public class SwingFrontend extends SystemB {

	public final UIPreferences ui;
	static JFrame f = new JFrame("Byransha");
	public final JPanel sheet = new JPanel(new MyLayout(Direction.VERTICAL));

	public SwingFrontend(BBGraph g) {
		super(g);
//		setLookAndFeel("WebLaf");
		g.systemNode.swing = this;
		g.systemNode.changeUserListener.add(u -> addNode(u));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		var width = (9 * screenSize.height) / 16;
		f.setSize(new Dimension(width, screenSize.height));
		f.setLocation((screenSize.width - width) / 2, 0);
		f.setVisible(true);
		sheet.setPreferredSize(f.getSize());
		var scroll = new JScrollPane(sheet, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(f.getSize());
		f.setContentPane(scroll);
		sheet.setBackground(Color.lightGray);

		// setNode(new AuthenticateAction(g));
		currentUser().listeners.add(n -> addNode(n));
		currentUser().jumpTo(currentUser());

		this.ui = new UIPreferences(g);
	}

	private void setLookAndFeel(String name) {
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");
		// https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes#how-to-use
		System.setProperty("flatlaf.useNativeFont", "false");
		com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme.setup();
	}

	int whatToDoViewCount = 0;

	public void addNode(BNode n) {
		for (int i = 0; i < whatToDoViewCount; ++i) {
			sheet.remove(sheet.getComponentCount() - 1);
		}

		var v = n.views().getFirst();
		var c = v.createComponent(n);
		addText("node " + n.id() + " " + n.prettyName() + " (" + n.whatIsThis() + ")");
		c.setForeground(v.getColor());
		sheet.add(c);
		whatToDoViewCount = sheet.getComponentCount();
		addText("What do you want to do now?");
		addText("Call some actions?");
		addWhatToDo(n, new AvailableActionsView(g));
		addText("Or see differents views of " + n.prettyName() + "?");
		addWhatToDo(n, new AvailableViewsView(g));
		whatToDoViewCount = sheet.getComponentCount() - whatToDoViewCount;

		f.getContentPane().revalidate();
		f.getContentPane().repaint();
	}

	private void addWhatToDo(BNode n, NodeView v) {
		try {
			var c = v.createComponent(n);
//			c.setBorder(new TitledBorder(v.whatItShows()));
			sheet.add(c);
		} catch (Throwable e) {
			g.systemNode.errorLog.add(e);
		}
	}

	private void addText(String s) {
		var c = new JTextField(s);
		c.setEditable(false);
		sheet.add(c);
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
