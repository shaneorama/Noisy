package test.contrib.colorcombo;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.*;

public class ColorPickerComboBoxRenderer extends JLabel implements
		ListCellRenderer {
	private Hashtable<String, ColorIcon> _colorStringToColorIcon;

	public ColorPickerComboBoxRenderer() {
		setOpaque(false);
		_colorStringToColorIcon = new Hashtable<String, ColorIcon>();
		fillColorTable();
	}

	private void fillColorTable() {
		for (String colorString : ColorPickerComboBox.STRING_TO_AWT_COLORS
				.keySet())
			_colorStringToColorIcon.put(colorString, new ColorIcon(
					ColorPickerComboBox.STRING_TO_AWT_COLORS.get(colorString)));
	}

	/*
	 * This method finds the image and text corresponding to the selected value
	 * and returns the label, set up to display the text and image.
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		ColorIcon icon = _colorStringToColorIcon.get(value);
		icon.setHeight(10);
		icon.setWidth(10);
		setIcon(icon);
		setText((String) value);
		setFont(list.getFont());

		return this;
	}

}
