package test.issues;

import java.awt.ComponentOrientation;

import javax.swing.*;

public class Issue397 {
	public static void main(String[] args) throws Exception {
		UIManager
				.setLookAndFeel("org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
				slider
						.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				slider.setMajorTickSpacing(20);
				slider.setPaintTicks(true);
				slider.setPaintLabels(true);
				JDialog dialog = new JDialog((JFrame) null, true);
				dialog.setLayout(null);
				dialog.add(slider);
				slider.setBounds(10, 10, 200, 200);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setSize(250, 250);
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);
			}
		});
	}
}
