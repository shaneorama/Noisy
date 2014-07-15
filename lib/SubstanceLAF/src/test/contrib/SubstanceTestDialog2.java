package test.contrib;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel;

/**
 * 
 * @author blongstr
 */
public class SubstanceTestDialog2 {

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
				final JPanel mainPanel = new JPanel() {

					@Override
					public Color getBackground() {
						return null;
					}
				};

				mainPanel.setLayout(new BorderLayout());

				final JPanel panel = new JPanel();
				panel.setBackground(Color.white);
				panel.setLayout(new FlowLayout(FlowLayout.CENTER));
				final JButton button = new JButton(
						"Click this button to get error");
				button.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(MouseEvent e) {
						final JPanel panel = new JPanel();
						panel.setLayout(new GridLayout(2, 1));
						panel.add(new JLabel("Testing 1,2, 3"));
						panel.add(new JTable(4, 3));
						panel.setSize(400, 600);
						panel.addNotify();
						panel.validate();

						// create image
						final BufferedImage image = new BufferedImage(400, 600,
								BufferedImage.TYPE_INT_ARGB);

						// render image
						final Graphics2D g2 = image.createGraphics();
						g2.setColor(Color.WHITE);
						g2.fillRect(0, 0, 400, 600);

						// print
						panel.printAll(g2);
						g2.dispose();
					}
				});
				panel.add(button);

				mainPanel.add(new JScrollPane(panel));

				appFrame.add(mainPanel);
			}
		});
	}
}
