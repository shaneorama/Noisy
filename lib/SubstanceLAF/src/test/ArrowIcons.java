package test;

import java.awt.*;

import javax.swing.*;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.colorscheme.AquaColorScheme;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.utils.SubstanceImageCreator;
import org.jvnet.substance.utils.SubstanceSizeUtils;

public class ArrowIcons extends JFrame {
	public ArrowIcons() {
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
			for (int i = 11; i < 35; i += 2) {
				Icon icon = SubstanceImageCreator.getArrowIcon(
						SubstanceSizeUtils.getSmallArrowIconWidth(i),
						SubstanceSizeUtils.getSmallArrowIconHeight(i),
						SubstanceSizeUtils.getDoubleArrowStrokeWidth(i),
						SwingConstants.SOUTH, st);
				icon.paintIcon(CPanel.this, g, x, 100);
				g.setColor(Color.blue);
				g.drawRect(x, 100, icon.getIconWidth(), icon.getIconHeight());

				icon = SubstanceImageCreator.getDoubleArrowIcon(i,
						SubstanceSizeUtils.getSmallArrowIconWidth(i),
						SubstanceSizeUtils.getSmallArrowIconHeight(i) + 3,
						SubstanceSizeUtils.getDoubleArrowStrokeWidth(i),
						SwingConstants.SOUTH, st);
				icon.paintIcon(CPanel.this, g, x, 140);
				g.setColor(Color.blue);
				g.drawRect(x, 140, icon.getIconWidth(), icon.getIconHeight());

				icon = SubstanceImageCreator.getArrowIcon(SubstanceSizeUtils
						.getArrowIconWidth(i), SubstanceSizeUtils
						.getArrowIconHeight(i), SubstanceSizeUtils
						.getArrowStrokeWidth(i), SwingConstants.SOUTH, st);
				icon.paintIcon(CPanel.this, g, x, 180);
				g.setColor(Color.blue);
				g.drawRect(x, 180, icon.getIconWidth(), icon.getIconHeight());

				icon = SubstanceImageCreator.getDoubleArrowIcon(i,
						SubstanceSizeUtils.getArrowIconWidth(i),
						SubstanceSizeUtils.getArrowIconHeight(i) + 3,
						SubstanceSizeUtils.getDoubleArrowStrokeWidth(i),
						SwingConstants.SOUTH, st);
				icon.paintIcon(CPanel.this, g, x, 220);
				g.setColor(Color.blue);
				g.drawRect(x, 220, icon.getIconWidth(), icon.getIconHeight());

				x += (i + 2);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ArrowIcons().setVisible(true);
			}
		});
	}

}
