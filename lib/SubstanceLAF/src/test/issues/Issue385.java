package test.issues;

//package test.issues;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel;

public class Issue385 extends JFrame {

	JDialog dialog;

	public Issue385() {
		super();
		this.setSize(new Dimension(200, 200));
		dialog = new JDialog(this);
		dialog.add(new JLabel("I'm a dialog"));
		dialog.setSize(new Dimension(100, 100));
		JButton launchDialogButton = new JButton("Launch Dialog");
		launchDialogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setLocationRelativeTo(Issue385.this);
				dialog.setVisible(true);
			}
		});
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(launchDialogButton);
		this.setVisible(true);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceCremeCoffeeLookAndFeel());
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		System.setProperty("sun.awt.noerasebackground", "true");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue385();
			}
		});
	}

}
