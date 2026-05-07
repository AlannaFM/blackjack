package blackjack;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

// jPanel com a rolagem vertical mais fluida  (não permite rolagem horizontal)

public class PainelRolavel extends JPanel implements Scrollable {

    @Override
    public Dimension getPreferredScrollableViewportSize() { return super.getPreferredSize(); }

    @Override
    public int getScrollableUnitIncrement(Rectangle rect, int orientation, int direction) { return 16; }

    @Override
    public int getScrollableBlockIncrement(Rectangle rect, int orientation, int direction) { return 16; }

    @Override
    public boolean getScrollableTracksViewportWidth()  { return true; }

    @Override
    public boolean getScrollableTracksViewportHeight() { return false; }
}