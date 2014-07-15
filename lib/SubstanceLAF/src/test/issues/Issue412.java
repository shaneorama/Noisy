package test.issues;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessSkin;

public class Issue412 extends JFrame {
	public Issue412() {
		super("Test big icon");

		BufferedImage image = new BufferedImage(48, 48,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setPaint(new GradientPaint(0, 0, Color.red, 48, 48, Color.blue));
		g2d.fillRect(0, 0, 48, 48);
		g2d.dispose();

		this.setIconImage(new ImageIcon(getClass().getResource("icon.png"))
				.getImage());

		this.setSize(200, 100);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public static void main(String[] args) throws Exception {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		SubstanceLookAndFeel.setSkin(new BusinessSkin());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue412().setVisible(true);
			}
		});
	}
}
