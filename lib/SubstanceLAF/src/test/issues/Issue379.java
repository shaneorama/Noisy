package test.issues;

import java.awt.HeadlessException;

import javax.swing.*;

public class Issue379 extends JFrame {
	private final JMenu mnMenu = new JMenu();

	private final JMenuItem mnOpen = new JMenuItem();

	private final JMenuItem mnClose = new JMenuItem();

	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Issue379 frame = new Issue379();

				frame.setVisible(true);
			}
		});
	}

	public Issue379() throws HeadlessException {
		mnMenu.add(mnOpen);
		mnMenu.add(mnClose);

		JMenuBar mnMain = new JMenuBar();

		mnMain.add(mnMenu);

		setJMenuBar(mnMain);

		pack(); // It's a reason of the problem

		mnMenu.setText("mnMenu");
		mnOpen.setText("mnOpen");
		mnClose.setText("mnClose");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(300, 200);
	}
}