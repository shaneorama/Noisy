package test.issues;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceRavenLookAndFeel;

public class Issue371 extends JFrame {
	private final JMenu menu;
	private final JLabel label;
	private final JButton button;

	public Issue371() throws HeadlessException {
		super("Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar menuBar = new JMenuBar();
		menu = new JMenu("File");
		menuBar.add(menu);
		setJMenuBar(menuBar);
		label = new JLabel("This is a test");
		add(label);
		button = new JButton("Test");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doStuff();
			}
		});
		add(button, BorderLayout.SOUTH);
		setBounds(100, 100, 500, 500);
		setVisible(true);
	}

	private void doStuff() {
		setChildrenEnabled(menu, false);
		setChildrenEnabled(label, false);
		setChildrenEnabled(button, false);
		final BufferedImage bluredFrame = gaussianBlurImage(
				getBufferedImageOfFrame(this), 3);
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(bluredFrame, 0, 0, bluredFrame.getWidth(),
						bluredFrame.getHeight(), null);
			}
		};
		setGlassPane(panel);
		getGlassPane().setVisible(true);
	}

	private static void setChildrenEnabled(Component component,
			boolean enabled, Component... exceptions) {
		Set<Component> excludedSet = new HashSet<Component>(Arrays
				.asList(exceptions));
		if (!excludedSet.contains(component))
			component.setEnabled(enabled);
		if (component instanceof Container) {
			Container container = (Container) component;
			for (int i = 0; i < container.getComponentCount(); i++)
				setChildrenEnabled(container.getComponent(i), enabled,
						exceptions);
		}
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
				new Issue371();
			}
		});
	}
}
