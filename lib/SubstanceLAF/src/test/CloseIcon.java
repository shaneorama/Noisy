package test;

import java.awt.*;

import javax.swing.*;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.colorscheme.AquaColorScheme;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.utils.SubstanceImageCreator;

public class CloseIcon extends JFrame {
	public CloseIcon() {
		this.add(new CPanel(), BorderLayout.CENTER);
		setSize(800, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static class CPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.lightGray.brighter());
			g.fillRect(0, 0, getWidth(), getHeight());
			SubstanceColorScheme st = new AquaColorScheme();
			int x = 0;
			for (int i = 12; i < 36; i++) {
				Icon icon = SubstanceImageCreator.getCloseIcon(i, st, st);
				icon.paintIcon(CPanel.this, g, x, 100);
				g.setColor(Color.blue);
				g.drawRect(x, 100, icon.getIconWidth(), icon.getIconHeight());
				icon = SubstanceImageCreator.getMaximizeIcon(i, st, st);
				icon.paintIcon(CPanel.this, g, x, 140);
				icon = SubstanceImageCreator.getMinimizeIcon(i, st, st);
				icon.paintIcon(CPanel.this, g, x, 180);
				x += (i + 2);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new CloseIcon().setVisible(true);
			}
		});
	}

}
