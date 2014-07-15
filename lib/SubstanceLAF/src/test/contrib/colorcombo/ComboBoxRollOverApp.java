package test.contrib.colorcombo;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceCremeLookAndFeel;

public class ComboBoxRollOverApp {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame.setDefaultLookAndFeelDecorated(true);
				try {
					UIManager.setLookAndFeel(new SubstanceCremeLookAndFeel());
				} catch (UnsupportedLookAndFeelException e) {
					System.out.println("ERROR: Substance failed to load");
				}

				JFrame frame = new JFrame();
				JPanel panel = new JPanel();
				ColorPickerComboBox colorPickerComboBox = new ColorPickerComboBox();
				panel.add(colorPickerComboBox);
				frame.add(panel);
				frame.pack();
				frame.setVisible(true);
			}
		});

	}
}
