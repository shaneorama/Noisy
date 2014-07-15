package test;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.*;

public class Label extends JFrame {
	public Label() {
		super("Some simple text");

		this.setLayout(new FlowLayout());

		JLabel label = new JLabel("sample");
		label.setForeground(Color.blue.darker());
		label.setBackground(Color.yellow);
		label.setOpaque(true);
		this.add(label);

		this.setSize(200, 100);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Label().setVisible(true);
			}
		});
	}
}
