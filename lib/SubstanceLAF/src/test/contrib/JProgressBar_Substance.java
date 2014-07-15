package test.contrib;

import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

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

public class JProgressBar_Substance {

	private static JProgressBar progressBar;

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
					ComponentState.SELECTED);
			this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
					DecorationAreaType.NONE);

			this.buttonShaper = new ClassicButtonShaper();
			this.gradientPainter = new ClassicGradientPainter();
			this.borderPainter = new ClassicBorderPainter();
			this.decorationPainter = new ClassicDecorationPainter();
			this.highlightPainter = new ClassicHighlightPainter();
		}

		@Override
		public String getDisplayName() {
			return "Custom";
		}
	}

	private static void initLookAndFeel() throws IOException {
		if (!SubstanceLookAndFeel.setSkin(new CustomSkin())) {
			System.err.println("Failed to set skin");
		}
	}

	/**
	 * Create the GUI and show it.
	 */
	private static void createAndShowGUI() throws ParseException, IOException {

		// Set the look and feel.
		initLookAndFeel();

		// make sure we have nice window decorations for the heck of it
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("JProgressBar_Substance");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set up progress bar, initial foreground color red
		JProgressBar progressBar = new JProgressBar(0, 100);
		// progressBar.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR,
		// 1.0);
		// progressBar.setForeground(Color.RED);
		// progressBar.setBackground(Color.BLUE);
		progressBar.setValue(50);
		progressBar.setStringPainted(true);

		// Put progress bar on the frame
		frame.add(progressBar);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					try {
						createAndShowGUI();
					} catch (IOException ex) {
						Logger
								.getLogger(
										JProgressBar_Substance.class.getName())
								.log(Level.SEVERE, null, ex);
					}
				} catch (ParseException ex) {
					Logger.getLogger(JProgressBar_Substance.class.getName())
							.log(Level.SEVERE, null, ex);
				}
			}
		});
	}

}
