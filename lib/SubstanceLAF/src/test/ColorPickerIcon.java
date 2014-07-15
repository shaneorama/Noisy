package test;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.*;

import org.jvnet.substance.utils.SubstanceCoreUtilities;

public class ColorPickerIcon extends JFrame {
	Icon orig;

	public ColorPickerIcon() {
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);

				Graphics2D g2d = (Graphics2D) g.create();

				g2d.setColor(Color.black);
				g2d.translate(-4, -6);
				int xc = 20;
				int yc = 22;
				int r = 15;

				g2d.setStroke(new BasicStroke(2.5f));
				g2d.drawOval(xc - r, yc - r, 2 * r, 2 * r);
				g2d.setStroke(new BasicStroke(4.0f));
				GeneralPath handle = new GeneralPath();
				handle.moveTo((float) (xc + r / Math.sqrt(2.0)),
						(float) (yc + r / Math.sqrt(2.0)));
				handle.lineTo(45, 47);
				g2d.draw(handle);
				g2d.translate(4, 6);

				g2d.dispose();
			}
		};
		this.add(panel, BorderLayout.CENTER);
		this.setSize(100, 100);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new ColorPickerIcon().setVisible(true);

				BufferedImage blank = SubstanceCoreUtilities.getBlankImage(48,
						48);
				Graphics2D g2d = blank.createGraphics();
				g2d.setColor(Color.black);
				g2d.translate(-4, -6);
				int xc = 20;
				int yc = 22;
				int r = 15;

				g2d.setStroke(new BasicStroke(2.5f));
				g2d.drawOval(xc - r, yc - r, 2 * r, 2 * r);
				g2d.setStroke(new BasicStroke(4.0f));
				GeneralPath handle = new GeneralPath();
				handle.moveTo((float) (xc + r / Math.sqrt(2.0)),
						(float) (yc + r / Math.sqrt(2.0)));
				handle.lineTo(45, 47);
				g2d.draw(handle);
				g2d.translate(4, 6);

				// try {
				// ImageIO
				// .write(
				// blank,
				// "png",
				// new File(
				// "C:\\JProjects\\substance\\src\\contrib\\ch\\randelshofer\\quaqua\\images\\zoomer.png"));
				// } catch (Exception exc) {
				// exc.printStackTrace();
				// }
			}
		});
	}

}
