package test.contrib;

import java.awt.*;
import java.util.Vector;

import javax.swing.*;

public class SubstanceJComboBoxTest extends JFrame {

	public static void main(String[] args) {
		// First set the look and feel - Substance based or system default.
		int selection = JOptionPane.showConfirmDialog(null,
				"Use a Substance-based look and feel?",
				"Select a Look And Feel", JOptionPane.YES_NO_OPTION);
		try {
			if (JOptionPane.YES_OPTION == selection) {
				UIManager
						.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
			} else {
				// UIManager.setLookAndFeel(UIManager.
				// getSystemLookAndFeelClassName());
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Open the screen.
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					new SubstanceJComboBoxTest();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	//==========================================================================
	// ====

	private SubstanceJComboBoxTest() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		Container theContainer = getContentPane();
		theContainer.setLayout(new BorderLayout());

		// Add an editable combo box.
		Vector<String> tempData = new Vector<String>();
		tempData.add("uuuuvvvvwwwwxxxxyyyyzzzz");
		tempData.add("uuuuvvvvwwwwxxxxyyyyzzzz");
		JComboBox theAppURLField = new JComboBox(tempData);
		theAppURLField.setEditable(true);
		theAppURLField
				.getEditor()
				.setItem(
						"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789zxywvutsrqponmlkjihgfedcba9876543210");
		theContainer.add(theAppURLField, BorderLayout.EAST);

//		System.out.println(((Component) theAppURLField.getEditor())
	//			.getPreferredSize());

		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
