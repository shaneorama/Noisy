package test;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;

public class Walkthrough extends JFrame {
	public Walkthrough() {
		super("Sample app");
		this.setLayout(new FlowLayout());
		this.add(new JButton("button"));
		this.add(new JCheckBox("check"));
		this.add(new JLabel("label"));

		this.setSize(new Dimension(250, 80));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Walkthrough w = new Walkthrough();
				w.setVisible(true);
			}
		});
	}
}