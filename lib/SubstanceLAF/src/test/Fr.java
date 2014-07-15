package test;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Fr extends JFrame {
	public Fr() {
		super("Some simple text");

		this.setSize(650, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Fr().setVisible(true);
			}
		});
	}
}
