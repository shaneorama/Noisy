package test.contrib;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class SmallDialogSubstance {

	private JDialog dlgMain;

	public SmallDialogSubstance() {
		try {
			UIManager
					.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());

			JDialog.setDefaultLookAndFeelDecorated(true);
			JFrame.setDefaultLookAndFeelDecorated(true);

			UIDefaults uiDef = UIManager.getDefaults();
			uiDef.put("RootPaneUI", "test.contrib.RootPaneUI");

			dlgMain = new JDialog((Window) null, "this title isn't shown.");

			dlgMain.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dlgMain.setLayout(new BorderLayout());
			dlgMain.add(new JLabel("Hello world!"), BorderLayout.CENTER);
			dlgMain.pack();
			dlgMain.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				new SmallDialogSubstance();
			}
		});
	}
}