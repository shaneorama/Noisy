package test.issues;

import java.awt.Font;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.CremeSkin;
import org.jvnet.substance.skin.SubstanceBusinessLookAndFeel;

public class Issue394 extends JFrame {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					SubstanceLookAndFeel feel = new SubstanceBusinessLookAndFeel();
					UIManager.setLookAndFeel(feel);
					SubstanceLookAndFeel.setSkin(new CremeSkin());// <- change
					// skin
				} catch (UnsupportedLookAndFeelException e) {
				}
				JComboBox comboBox = new JComboBox();
				comboBox.setFont(new Font("Dialog", Font.BOLD, 12));
				// <- so when we set new font, NPE will cause in old
				// comboboxUI (uninstalled from combo)

				Issue394 thisClass = new Issue394();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.getContentPane().add(comboBox);
				thisClass.pack();
				thisClass.setLocationRelativeTo(null);
				thisClass.setVisible(true);
			}
		});
	}
}