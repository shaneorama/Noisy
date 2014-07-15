package test;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class EmptyFrame extends JFrame {
	public EmptyFrame() {
		this.add(new JTabbedPane());

		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws Exception {
		JFrame.setDefaultLookAndFeelDecorated(true);
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new EmptyFrame().setVisible(true);
			}
		});
	}
}
