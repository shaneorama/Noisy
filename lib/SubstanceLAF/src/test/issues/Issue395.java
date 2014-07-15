package test.issues;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.CremeSkin;
import org.jvnet.substance.skin.SubstanceBusinessLookAndFeel;

public class Issue395 extends JFrame {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					SubstanceLookAndFeel feel = new SubstanceBusinessLookAndFeel();
					UIManager.setLookAndFeel(feel);
				} catch (UnsupportedLookAndFeelException e) {
				}
				Issue395 thisClass = new Issue395();
				thisClass.setLocationRelativeTo(null);
				// if we comment this line, there will be no exception
				thisClass.setVisible(true);
				SubstanceLookAndFeel.setSkin(new CremeSkin());
				// if we comment this line, there will be no exception too :)
			}
		});
	}
}