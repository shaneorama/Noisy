package test.contrib;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel;

public class WalkThrough extends JFrame {
	public WalkThrough() {
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

				try {
					UIManager
							.setLookAndFeel(new SubstanceRavenGraphiteLookAndFeel());
				} catch (Exception e) {
					e.printStackTrace();
					System.out
							.println("Substance Raven Graphite failed to initialize");
				}
				WalkThrough w = new WalkThrough();
				w.setVisible(true);
			}
		});
	}
}
