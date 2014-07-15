package test;

import java.awt.*;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.utils.SubstanceTextUtilities;

public class VerticalTextsDefault extends JFrame {
	protected static class VerticalPanel extends JPanel {
		private Font font;

		public VerticalPanel(Font font) {
			this.font = font;
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0, 0, getWidth(), getHeight());

			Rectangle[] rectsT2B = new Rectangle[] {
					new Rectangle(40, 10, 20, 100),
					new Rectangle(130, 100, 20, 100),
					new Rectangle(10, 40, 20, 100) };
			Rectangle[] rectsB2T = new Rectangle[] {
					new Rectangle(80, 10, 20, 100),
					new Rectangle(10, 150, 20, 100),
					new Rectangle(50, 120, 20, 100) };
			int count = 0;
			g.setColor(Color.blue);
			for (Rectangle rect : rectsT2B)
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
			g.setColor(Color.red);
			for (Rectangle rect : rectsB2T)
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
			for (Rectangle rect : rectsT2B) {
				SubstanceTextUtilities.paintVerticalText(g, this, rect,
						"Top to bottom text " + (++count), -1, this.font,
						Color.red, null, false);
			}
			for (Rectangle rect : rectsB2T) {
				SubstanceTextUtilities.paintVerticalText(g, this, rect,
						"Bottom to top text " + (++count), -1, this.font,
						Color.blue, null, true);
			}
		}
	}

	public VerticalTextsDefault() {
		super("Vertical Swing texts");
		this.setLayout(new BorderLayout());
		this.add(new VerticalPanel(new Font("Segoe UI", Font.PLAIN, 12)),
				BorderLayout.CENTER);

		this.setSize(400, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new VerticalTextsDefault().setVisible(true);
			}
		});
	}

}
