package test.issues;

import org.jvnet.substance.skin.SubstanceRavenLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Issue374 extends JFrame {
	public Issue374(final boolean quick) throws HeadlessException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JButton button = new JButton("This is a button");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button.setEnabled(false);
				if (quick)
					blur();
				else
					new Thread() {
						public void run() {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
									blur();
								}
							});
						}
					}.start();
			}
		});
		add(button);
		if (quick)
			setBounds(0, 0, 200, 80);
		else
			setBounds(200, 0, 200, 80);
		setVisible(true);
	}

	private void blur() {
		final BufferedImage bluredFrame = gaussianBlurImage(
				getBufferedImageOfFrame(this), 3);
		JPanel panel = new JPanel() {
			protected void paintComponent(Graphics g) {
				g.drawImage(bluredFrame, 0, 0, bluredFrame.getWidth(),
						bluredFrame.getHeight(), null);
			}
		};
		setGlassPane(panel);
		getGlassPane().setVisible(true);
	}

	private static ConvolveOp getGaussianBlurFilter(int radius,
			boolean horizontal) {
		if (radius < 1)
			throw new IllegalArgumentException("Radius must be >= 1");
		int size = radius * 2 + 1;
		float[] data = new float[size];
		float sigma = radius / 3.0f;
		float twoSigmaSquare = 2.0f * sigma * sigma;
		float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
		float total = 0.0f;
		for (int i = -radius; i <= radius; i++) {
			float distance = i * i;
			int index = i + radius;
			data[index] = (float) Math.exp(-distance / twoSigmaSquare)
					/ sigmaRoot;
			total += data[index];
		}
		for (int i = 0; i < data.length; i++)
			data[i] /= total;
		Kernel kernel;
		if (horizontal)
			kernel = new Kernel(size, 1, data);
		else
			kernel = new Kernel(1, size, data);
		return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
	}

	private static BufferedImage getBufferedImageOfFrame(JFrame frame) {
		JLayeredPane layeredPane = frame.getRootPane().getLayeredPane();
		BufferedImage image = new BufferedImage(layeredPane.getWidth(),
				layeredPane.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.getGraphics();
		layeredPane.paint(graphics);
		graphics.dispose();
		return image;
	}

	public static BufferedImage gaussianBlurImage(BufferedImage image,
			int radius) {
		BufferedImage bluredImage = getGaussianBlurFilter(radius, true).filter(
				image, null);
		bluredImage = getGaussianBlurFilter(radius, false).filter(bluredImage,
				null);
		return bluredImage;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceRavenLookAndFeel());
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				new Issue374(true);
				new Issue374(false);
			}
		});
	}
}