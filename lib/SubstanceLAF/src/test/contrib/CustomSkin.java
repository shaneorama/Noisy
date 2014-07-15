package test.contrib;

import javax.swing.*;

import org.jvnet.substance.api.*;
import org.jvnet.substance.colorscheme.*;
import org.jvnet.substance.painter.border.ClassicBorderPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.gradient.GlassGradientPainter;
import org.jvnet.substance.painter.highlight.GlassHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;
import org.jvnet.substance.skin.ModerateSkin;
import org.jvnet.substance.skin.SubstanceModerateLookAndFeel;

import test.check.SampleFrame;

public class CustomSkin extends ModerateSkin {

	private String Skin_NAME = "Custom Skin";

	public static void main(String[] args)
			throws UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(new SubstanceModerateLookAndFeel());
		SubstanceModerateLookAndFeel.setSkin(new CustomSkin());
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				JFrame sf = new SampleFrame(false);
				sf.setSize(315, 245);
				sf.setLocationRelativeTo(null);
				sf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				sf.setVisible(true);
			}
		});

	}

	public CustomSkin() {
		try {
			SubstanceColorScheme activeScheme = new SteelBlueColorScheme();
			SubstanceColorScheme defaultScheme = new MetallicColorScheme();
			SubstanceColorScheme disabledScheme = new LightGrayColorScheme();
			SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
					activeScheme, defaultScheme, disabledScheme);
			// Here I am trying to set the
			// default color of Button to Brown but It is picking the color
			// which is stored in defaultScheme.
			defaultSchemeBundle.registerColorScheme(new BrownColorScheme(),
					ComponentState.DEFAULT);

			this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
					DecorationAreaType.NONE);

			this.borderPainter = new ClassicBorderPainter();
			this.gradientPainter = new GlassGradientPainter();
			this.buttonShaper = new ClassicButtonShaper();
			this.highlightPainter = new GlassHighlightPainter();
			SubstanceColorSchemeBundle headerSchemeBundle = new SubstanceColorSchemeBundle(
					activeScheme, new BottleGreenColorScheme().tint(0.60),
					disabledScheme);

			this.registerDecorationAreaSchemeBundle(headerSchemeBundle,
					DecorationAreaType.HEADER);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getDisplayName() {
		return Skin_NAME;
	}
}
