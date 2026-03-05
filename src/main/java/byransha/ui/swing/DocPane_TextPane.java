package byransha.ui.swing;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class DocPane_TextPane extends JTextPane implements ByranshaUserPane {

	@Override
	public boolean getScrollableTracksViewportWidth() {
		// Force le composant à toujours faire la largeur du JScrollPane
		// Cela oblige le texte à revenir à la ligne verticalement.
		return true;
	}

	@Override
	public void append(String s) {
		StyledDocument doc = getStyledDocument();

		try {
			doc.insertString(doc.getLength(), s, null);
		} catch (BadLocationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void append(JComponent c) {
		setCaretPosition(getDocument().getLength());
		insertComponent(c);
		setCaretPosition(getDocument().getLength());
	}

	@Override
	public void newLine() {
		setCaretPosition(getDocument().getLength());
		replaceSelection("\n");
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void clear() {
		setText("");
	}

}
