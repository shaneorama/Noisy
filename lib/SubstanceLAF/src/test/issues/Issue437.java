package test.issues;

import java.awt.BorderLayout;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;

public class Issue437 extends JFrame {
	public Issue437() {
		JTabbedPane jtp = new JTabbedPane();
		jtp.addTab("one", new JPanel());
		jtp.addTab("two", new JPanel());
		jtp.addTab("three", new JPanel());

		this.add(jtp, BorderLayout.CENTER);
		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SubstanceLookAndFeel.setSkin(new Issue437Skin());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue437().setVisible(true);
			}
		});
	}

}
