package test.issues;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.colorscheme.BaseLightColorScheme;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;

public class Issue449 extends JFrame {
	private static class Skin449 extends BusinessBlackSteelSkin {
		@Override
		public String getDisplayName() {
			return "Skin 449";
		}

		public Skin449() {
			SubstanceColorScheme scheme = new BaseLightColorScheme("Scheme 449") {
				Color bg = Color.blue;

				@Override
				public Color getUltraDarkColor() {
					return bg;
				}

				@Override
				public Color getDarkColor() {
					return bg;
				}

				@Override
				public Color getMidColor() {
					return bg;
				}

				@Override
				public Color getLightColor() {
					return bg;
				}

				@Override
				public Color getExtraLightColor() {
					return bg;
				}

				@Override
				public Color getUltraLightColor() {
					return bg;
				}

				@Override
				public Color getForegroundColor() {
					return Color.white;
				}
			};

			this.registerAsDecorationArea(scheme,
					DecorationAreaType.PRIMARY_TITLE_PANE);
		}
	}

	public Issue449() {
		super("Issue 449");

		this.setSize(300, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SubstanceLookAndFeel.setSkin(new Skin449());
				new Issue449().setVisible(true);
			}
		});
	}

}
