package byransha.ui.swing;
import java.awt.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.*;

public class WrapLayout implements LayoutManager {
    private int hgap;
    private int vgap;

    public WrapLayout(int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    /**
     * Crée un composant invisible qui servira de signal de retour à la ligne.
     */
    public static Component createLineBreak() {
        JComponent breakComp = (JComponent) Box.createHorizontalStrut(Integer.MAX_VALUE);
        breakComp.setName("FORCE_WRAP"); // On utilise le nom comme marqueur
        return breakComp;
    }

    private boolean isForceWrap(Component c) {
        return "FORCE_WRAP".equals(c.getName());
    }

    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxWidth = target.getWidth() - (insets.left + insets.right);
            int x = insets.left;
            int y = insets.top;
            int rowHeight = 0;

            for (Component m : target.getComponents()) {
                if (m.isVisible()) {
                    // SI c'est notre marqueur de saut de ligne
                    if (isForceWrap(m)) {
                        x = insets.left;
                        y += vgap + rowHeight;
                        rowHeight = 0;
                        m.setBounds(0, 0, 0, 0); // Le marqueur ne prend pas de place
                        continue;
                    }

                    Dimension d = m.getPreferredSize();

                    // Saut de ligne AUTOMATIQUE (si le composant dépasse)
                    if (x > insets.left && x + d.width > maxWidth + insets.left) {
                        x = insets.left;
                        y += vgap + rowHeight;
                        rowHeight = 0;
                    }

                    m.setBounds(x, y, d.width, d.height);
                    x += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target);
    }

    private Dimension layoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;

            Insets insets = target.getInsets();
            int maxWidth = targetWidth - (insets.left + insets.right);

            int x = 0;
            int y = insets.top;
            int rowHeight = 0;
            int totalWidth = 0;

            for (Component m : target.getComponents()) {
                if (m.isVisible()) {
                    if (isForceWrap(m)) {
                        x = 0;
                        y += vgap + rowHeight;
                        rowHeight = 0;
                        continue;
                    }

                    Dimension d = m.getPreferredSize();
                    if (x > 0 && x + d.width > maxWidth) {
                        x = 0;
                        y += vgap + rowHeight;
                        rowHeight = 0;
                    }

                    if (x > 0) x += hgap;
                    x += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                    totalWidth = Math.max(totalWidth, x);
                }
            }
            return new Dimension(totalWidth + insets.left + insets.right, y + rowHeight + insets.bottom);
        }
    }

    @Override public void addLayoutComponent(String name, Component comp) {}
    @Override public void removeLayoutComponent(Component comp) {}
    @Override public Dimension minimumLayoutSize(Container target) { return preferredLayoutSize(target); }
}