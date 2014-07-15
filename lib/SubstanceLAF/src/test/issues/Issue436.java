package test.issues;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel;

/**
 * 
 * @author blongstr
 */
public class Issue436 {

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				UIManager.getDefaults().put(
						SubstanceLookAndFeel.COLORIZATION_FACTOR,
						new Double(1.0d));

				try {
					UIManager
							.setLookAndFeel(new SubstanceMistSilverLookAndFeel());
				} catch (UnsupportedLookAndFeelException ex) {
					ex.printStackTrace();
				}

				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);

				final JFrame appFrame = new JFrame("Substance Test Application");
				appFrame.addWindowListener(new java.awt.event.WindowAdapter() {

					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});

				appFrame.setSize(400, 400);
				appFrame.setLocationRelativeTo(null);
				appFrame.setVisible(true);

				// Example of a component returning a null background color and
				// how it effects component UI initialization.
				JPanel panel = new JPanel() {

					@Override
					public Color getBackground() {
						return null;
					}
				};

				panel.setLayout(new FlowLayout());

				JButton button = new JButton("Test Button");
				panel.add(button);
				panel.add(new JLabel("Test Label"));

				appFrame.add(panel);
			}
		});
	}
}
