package test.contrib;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDesktopPane;

public class SnapListener implements ComponentListener {

	private static final int THRESHOLD = 8;

	private JDesktopPane pane;

	private boolean snapToBorder = true;

	private boolean locked = false;

	public SnapListener(JDesktopPane pane) {
		this.pane = pane;
		this.pane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
	}

	private static int getLeft(Component c) {
		return c.getX();
	}

	private static int getRight(Component c) {
		return getLeft(c) + c.getWidth();
	}

	private static int getTop(Component c) {
		return c.getY();
	}

	private static int getBottom(Component c) {
		return getTop(c) + c.getHeight();
	}

	private boolean isNear(int p1, int p2) {
		return Math.abs(p1 - p2) < THRESHOLD;
	}

	@Override
	public void componentHidden(ComponentEvent event) {
	}

	@Override
	public void componentMoved(ComponentEvent event) {
		if (locked) {
			return;
		}
		Component c = event.getComponent();
		locked = true;
		if (snapToBorder) {
			// left border
			if (getLeft(c) < THRESHOLD) {
				c.setLocation(0, c.getLocation().y);
			}

			// upper border
			if (c.getLocation().y < THRESHOLD) {
				c.setLocation(c.getLocation().x, 0);
			}

			// right border
			if (isNear(getRight(c), pane.getWidth())) {
				setRight(c, pane.getWidth());
			}

			// lower border
			if (isNear(getBottom(c), pane.getHeight())) {
				setBottom(c, pane.getHeight());
			}

			// c.getParent().repaint();
		}

		locked = false;
	}

	private static void setTop(Component c, int y) {
		c.setLocation(c.getLocation().x, y);
	}

	private static void setLeft(Component c, int x) {
		c.setLocation(x, c.getLocation().y);
	}

	private static void setBottom(Component c, int y) {
		c.setLocation(c.getLocation().x, y - c.getHeight());
	}

	private static void setRight(Component c, int x) {
		c.setLocation(x - c.getWidth(), c.getLocation().y);
	}

	@Override
	public void componentResized(ComponentEvent event) {
	}

	@Override
	public void componentShown(ComponentEvent event) {
	}

}