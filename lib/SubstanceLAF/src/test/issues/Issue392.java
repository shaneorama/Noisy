package test.issues;

import java.awt.FlowLayout;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessSkin;

public class Issue392 extends JFrame {
	public Issue392() {
		super("Issue 392");
		this.setLayout(new FlowLayout());
		this.add(new JButton("sample"));

		this.setSize(200, 100);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SubstanceLookAndFeel.setSkin(new BusinessSkin());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue392().setVisible(true);
			}
		});
	}
}
