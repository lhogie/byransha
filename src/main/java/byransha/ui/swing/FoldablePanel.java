package byransha.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class FoldablePanel extends JPanel {
	private final JComponent content;
	private final JButton toggleButton;
	private final String title;
	private final int contentPreferredHeight;
	private final int width;

	public FoldablePanel(String title, JComponent content) {
		this.title = title;
		setLayout(new BorderLayout());
		var c = new GridBagConstraints();
		toggleButton = new JButton(title + " ▼");
		toggleButton.setHorizontalAlignment(SwingConstants.RIGHT);
		toggleButton.setContentAreaFilled(false);
		toggleButton.addActionListener(e -> toggle());
		add(toggleButton, BorderLayout.NORTH);

		this.content = content;
		add(content, BorderLayout.CENTER);

		this.contentPreferredHeight = content.getPreferredSize().height;
		this.width = content.getPreferredSize().width;
	}

	private void toggle() {
		boolean isVisible = content.isVisible();
		content.setVisible(!isVisible);
		toggleButton.setText(title + (isVisible ? " ▶" : " ▼"));
		revalidate();
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		var d = new Dimension();
		d.width = width;
		d.height = toggleButton.getPreferredSize().height + (content.isVisible() ? contentPreferredHeight : 0);
		return d;
	}
}