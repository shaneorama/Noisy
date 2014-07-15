package test;

import java.awt.BorderLayout;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.*;
import org.jvnet.substance.colorscheme.MetallicColorScheme;
import org.jvnet.substance.colorscheme.SunsetColorScheme;
import org.jvnet.substance.painter.border.ClassicBorderPainter;
import org.jvnet.substance.painter.decoration.ClassicDecorationPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.gradient.ClassicGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;

public class SelectedTabScheme extends JFrame {
	protected static class CustomSkin extends SubstanceSkin {
		public CustomSkin() {
			SubstanceColorScheme activeScheme = new MetallicColorScheme().tint(
					0.15).named("Custom Active");
			SubstanceColorScheme defaultScheme = new MetallicColorScheme()
					.shade(0.1).named("Custom Default");
			SubstanceColorScheme disabledScheme = defaultScheme;

			SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
					activeScheme, defaultScheme, disabledScheme);
			defaultSchemeBundle.registerColorScheme(disabledScheme, 0.4f,
					ComponentState.DISABLED_UNSELECTED);
			defaultSchemeBundle.registerColorScheme(activeScheme, 0.4f,
					ComponentState.DISABLED_SELECTED);
			defaultSchemeBundle.registerColorScheme(new SunsetColorScheme(),
					ColorSchemeAssociationKind.TAB, ComponentState.SELECTED);
			this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
					DecorationAreaType.NONE);

			this.buttonShaper = new ClassicButtonShaper();
			this.gradientPainter = new ClassicGradientPainter();
			this.borderPainter = new ClassicBorderPainter();
			this.decorationPainter = new ClassicDecorationPainter();
			this.highlightPainter = new ClassicHighlightPainter();

			// note the fade start / end for selected tabs
			this.selectedTabFadeStart = 1.0f;
			this.selectedTabFadeEnd = 1.0f;
		}

		@Override
		public String getDisplayName() {
			return "Custom";
		}
	}

	public SelectedTabScheme() {
		super("Selected tab");

		JTabbedPane jtp = new JTabbedPane();
		jtp.addTab("tab0", new JPanel());
		jtp.addTab("tab1", new JPanel());
		jtp.addTab("tab2", new JPanel());

		this.add(jtp, BorderLayout.CENTER);

		this.setSize(300, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SubstanceLookAndFeel.setSkin(new CustomSkin());
				new SelectedTabScheme().setVisible(true);
			}
		});
	}

}
