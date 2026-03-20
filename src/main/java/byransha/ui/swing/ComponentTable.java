package byransha.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ComponentTable extends JPanel {

	private final String[] headers;
	private int rowCount = 0;

	public ComponentTable(String... headers) {
		this.headers = headers;
		setLayout(new GridBagLayout());
		addHeaders();
	}

	private void addHeaders() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.insets = new Insets(2, 4, 2, 4);

		for (int i = 0; i < headers.length; i++) {
			c.gridx = i;
			JLabel header = new JLabel(headers[i]);
			header.setFont(header.getFont().deriveFont(Font.BOLD));
			header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY));
			add(header, c);
		}
		rowCount = 1;
	}

	public void addRow(Component... components) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = rowCount;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.insets = new Insets(2, 4, 2, 4);

		for (int i = 0; i < components.length; i++) {
			c.gridx = i;
			add(components[i], c);
		}

		// Push rows to top — fill remaining vertical space
		if (rowCount == 1) {
			GridBagConstraints filler = new GridBagConstraints();
			filler.gridy = 999;
			filler.weighty = 1.0;
			filler.gridx = 0;
			add(Box.createVerticalGlue(), filler);
		}

		rowCount++;
		revalidate();
		repaint();
	}

	public void removeRow(int row) {
		// row 0 is headers, data starts at 1
		Component[] all = getComponents();
		int targetRow = row + 1;
		for (Component comp : all) {
			GridBagConstraints c = ((GridBagLayout) getLayout()).getConstraints(comp);
			if (c.gridy == targetRow)
				remove(comp);
			// shift rows below up
			else if (c.gridy > targetRow) {
				c.gridy--;
				((GridBagLayout) getLayout()).setConstraints(comp, c);
			}
		}
		rowCount--;
		revalidate();
		repaint();
	}
}