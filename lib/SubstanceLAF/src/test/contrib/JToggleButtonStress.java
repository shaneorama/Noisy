package test.contrib;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel;

public class JToggleButtonStress extends JFrame {
	ButtonGroup buttonGroup = new ButtonGroup();

	public JToggleButtonStress() {

		setLayout(null);
		int y = 0;
		for (int i = 0; i < 25; i++) {
			JToggleButton toogleButton = new JToggleButton(
					"A test toggle button!");
			toogleButton.setBounds(0, y, 1280, 30);
			// toogleButton.putClientProperty(LafWidget.ANIMATION_KIND,
			// AnimationKind.NONE);
			y += 30;
			buttonGroup.add(toogleButton);
			add(toogleButton);

		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1280, 768);
		setVisible(true);
	}

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);
				try {
					UIManager
							.setLookAndFeel(new SubstanceOfficeSilver2007LookAndFeel());
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				new JToggleButtonStress();
			}

		});

	}
}
