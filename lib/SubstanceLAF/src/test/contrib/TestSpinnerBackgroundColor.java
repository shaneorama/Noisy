package test.contrib;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class TestSpinnerBackgroundColor {

	public TestSpinnerBackgroundColor() {

		try {
			UIManager
					.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
			JDialog.setDefaultLookAndFeelDecorated(true);
			JFrame.setDefaultLookAndFeelDecorated(true);

			JFrame frame = new JFrame("Test spinner backgroundcolor");

			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.add(new JLabel("Spinner"), BorderLayout.WEST);

			JSpinner spinner = new JSpinner();
			spinner.setBackground(Color.YELLOW);
			spinner.setBackground(Color.RED);
			frame.add(spinner, BorderLayout.CENTER);

			frame.pack();
			frame.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				new TestSpinnerBackgroundColor();
			}
		});
	}
}