package test;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import org.jvnet.substance.api.ComponentState;
import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.colorscheme.AquaColorScheme;
import org.jvnet.substance.painter.border.StandardBorderPainter;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.gradient.StandardGradientPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.utils.SubstanceImageCreator;

public class Checkmark extends JFrame {
	public Checkmark() {
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
			SubstanceGradientPainter gp = new StandardGradientPainter();
			SubstanceBorderPainter bp = new StandardBorderPainter();
			int x = 0;
			for (int i = 12; i < 36; i++) {
				JCheckBox jcb = new JCheckBox();
				jcb.setFont(new Font("Arial", Font.PLAIN, i));
				jcb.setSelected(true);
				BufferedImage cbi = SubstanceImageCreator.getCheckBox(jcb, gp,
						bp, i, ComponentState.SELECTED,
						ComponentState.SELECTED, st, st, st, st, st, st, 0,
						1.0f, false);
				g.setColor(new Color(255, 196, 196));
				g.fillRect(x, 100, cbi.getWidth(), cbi.getHeight());
				g.drawImage(SubstanceImageCreator.getCheckBox(jcb, gp, bp, i,
						ComponentState.SELECTED, ComponentState.SELECTED, st,
						st, st, st, st, st, 0, 1.0f, false), x, 100, null);
				x += (i + 2);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Checkmark().setVisible(true);
			}
		});
	}
}
