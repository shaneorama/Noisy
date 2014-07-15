package test.issues;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.skin.OfficeSilver2007Skin;
import org.jvnet.substance.skin.OfficeBlue2007Skin;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

public class Issue419 extends JFrame {

	public Issue419() {
		super("Internal Frame Icon test");

	}

	private static void changeSkin() {
		if (UIManager.getLookAndFeel() instanceof SubstanceLookAndFeel) {
			SubstanceSkin currentSkin = SubstanceLookAndFeel.getCurrentSkin();
			if (currentSkin instanceof OfficeSilver2007Skin)
				SubstanceLookAndFeel.setSkin(new OfficeBlue2007Skin());
			else
				SubstanceLookAndFeel.setSkin(new OfficeSilver2007Skin());
		} else {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
			}
			for (Window window : Window.getWindows()) {
				SwingUtilities.updateComponentTreeUI(window);
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		//JDialog.setDefaultLookAndFeelDecorated(true);
		//JFrame.setDefaultLookAndFeelDecorated(true);
		// SubstanceLookAndFeel.setSkin(new OfficeSilver2007Skin());
		Issue419 frameTest = new Issue419();
		JDesktopPane desktop = new JDesktopPane();
		desktop.setPreferredSize(new Dimension(400, 400));
		frameTest.add(desktop, BorderLayout.CENTER);
		JInternalFrame anInternalFrame = new JInternalFrame(
				"An internal frame", true, false, false, true);
		desktop.add(anInternalFrame);
		anInternalFrame.setPreferredSize(new Dimension(200, 200));
		anInternalFrame.pack();
		anInternalFrame.setFrameIcon(null);
		anInternalFrame.setLocation(0, 0);
		anInternalFrame.setVisible(true);
		JButton changeSkinButton = new JButton("Change skin");
		changeSkinButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeSkin();
			}
		});
		frameTest.add(changeSkinButton, BorderLayout.SOUTH);
		frameTest.pack();
		frameTest.setVisible(true);
	}
}
