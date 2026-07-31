package byransha.ui.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

public record PositionAndSize(Dimension size, Point location) implements Serializable {
}