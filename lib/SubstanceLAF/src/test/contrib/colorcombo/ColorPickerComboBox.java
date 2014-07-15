package test.contrib.colorcombo;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComboBox;

public class ColorPickerComboBox extends JComboBox {
	public static Hashtable<Color, String> AWT_COLOR_TO_STRING;
	public static Hashtable<String, Color> STRING_TO_AWT_COLORS;
	public static Color DEFAULT_COLOR;

	{
		AWT_COLOR_TO_STRING = new Hashtable<Color, String>();
		STRING_TO_AWT_COLORS = new Hashtable<String, Color>();
		AWT_COLOR_TO_STRING.put(Color.RED, "Red");
		AWT_COLOR_TO_STRING.put(Color.CYAN, "Cyan");
		AWT_COLOR_TO_STRING.put(Color.YELLOW, "Yellow");
		AWT_COLOR_TO_STRING.put(Color.BLUE, "Blue");
		AWT_COLOR_TO_STRING.put(Color.GRAY, "Gray");
		AWT_COLOR_TO_STRING.put(Color.GREEN, "Green");
		AWT_COLOR_TO_STRING.put(Color.ORANGE, "Orange");
		AWT_COLOR_TO_STRING.put(Color.PINK, "Pink");
		AWT_COLOR_TO_STRING.put(DEFAULT_COLOR = Color.DARK_GRAY, "Black");
		AWT_COLOR_TO_STRING.put(Color.WHITE, "White");
		Enumeration keys = AWT_COLOR_TO_STRING.keys();
		while (keys.hasMoreElements()) {
			Color currentColor = (Color) keys.nextElement();
			STRING_TO_AWT_COLORS.put(AWT_COLOR_TO_STRING.get(currentColor),
					currentColor);
		}
	}

	public ColorPickerComboBox() {
		for (Color color : AWT_COLOR_TO_STRING.keySet())
			super.addItem(AWT_COLOR_TO_STRING.get(color));
		setRenderer(new ColorPickerComboBoxRenderer());
	}

	public void setSelectedColor(Color color) {
		String colorString = AWT_COLOR_TO_STRING.get(color);
		if (colorString == null)
			colorString = AWT_COLOR_TO_STRING.get(DEFAULT_COLOR);
		setSelectedItem(colorString);
	}

	public Color getSelectedColor() {
		return STRING_TO_AWT_COLORS.get(getSelectedItem());
	}
}
