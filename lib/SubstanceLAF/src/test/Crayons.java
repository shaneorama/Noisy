package test;

import java.awt.*;

import javax.swing.*;

import org.jvnet.substance.utils.SubstanceImageCreator;

public class Crayons extends JPanel {
	private Image crayons;

	public Crayons() {
		this.crayons = SubstanceImageCreator.getCrayonsImage();
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(this.crayons, 0, 0, null);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Crayons");
				frame.add(new Crayons());
				frame.setSize(400, 400);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

}
