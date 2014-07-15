package test.contrib.colorcombo;

import java.awt.*;

import javax.swing.Icon;
import javax.swing.JComponent;

public class ColorIcon extends JComponent implements Icon {

	private int _width;
	private int _height;
	private Color _color;

	public ColorIcon(Color color) {
		_color = color;
	}

	public Color getColor() {
		return _color;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setColor(_color);
		g2d.fillRect(x + 1, y + 1, _width - 2, _height - 2);
		g2d.setColor(Color.black);
		g2d.drawRect(x + 1, y + 1, _width - 2, _height - 2);

		g2d.dispose();
	}

	public int getIconWidth() {
		return _width;
	}

	public int getIconHeight() {
		return _height;
	}

	public void setHeight(int height) {
		_height = height;
	}

	public void setWidth(int width) {
		_width = width;
	}
}
