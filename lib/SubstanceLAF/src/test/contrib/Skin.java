package test.contrib;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class Skin extends javax.swing.JFrame {
	public Skin() {
		initComponents();
	}

	private void initComponents() {
		jButton1 = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jButton1.setText("jButton1");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addContainerGap(302,
						Short.MAX_VALUE).addComponent(jButton1).addGap(25, 25,
						25)));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addContainerGap(254,
						Short.MAX_VALUE).addComponent(jButton1).addGap(23, 23,
						23)));

		pack();
	}

	public static void main(String args[]) {
		try {
			setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			UIManager
					.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Skin().setVisible(true);
			}
		});
	}

	private javax.swing.JButton jButton1;

}