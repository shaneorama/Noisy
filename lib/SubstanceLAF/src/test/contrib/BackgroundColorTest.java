/**
 * 
 */
package test.contrib;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.*;
import org.jvnet.substance.painter.border.GlassBorderPainter;
import org.jvnet.substance.painter.decoration.ArcDecorationPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.gradient.SimplisticGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;

/**
 * @author waligora
 * 
 */
public class BackgroundColorTest extends JFrame {

	public static void main(String[] args) {
		try {
			initLaF();
		} catch (Exception e) {
			System.out.println(e);
		}

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				new BackgroundColorTest("Test");
			}
		});
	}

	protected static class CustomSkin extends SubstanceSkin {

		public CustomSkin() {
			super();
			// DefaultColorScheme
			Color[] colors = { Color.white, new Color(150, 150, 150),
					new Color(170, 170, 170), new Color(130, 130, 130),
					new Color(100, 100, 100), new Color(76, 76, 76),
					new Color(101, 101, 101), new Color(108, 108, 108),
					new Color(170, 170, 232) };
			ParameterColorScheme defaultScheme = new ParameterColorScheme(
					colors);
			// active ColorScheme
			Color[] activeColors = { Color.white, new Color(170, 170, 232),
					new Color(165, 165, 212), new Color(117, 120, 232),
					new Color(100, 100, 100), new Color(76, 76, 76),
					new Color(101, 101, 101), new Color(108, 108, 108),
					new Color(170, 170, 232) };
			ParameterColorScheme activeScheme = new ParameterColorScheme(
					activeColors);
			// disabled ColorScheme:
			Color[] disabledColors = { new Color(167, 167, 167),
					new Color(150, 150, 150), new Color(170, 170, 170),
					new Color(130, 130, 130), new Color(100, 100, 100),
					new Color(76, 76, 76), new Color(101, 101, 101),
					new Color(108, 108, 108), new Color(170, 170, 232) };
			ParameterColorScheme disabledScheme = new ParameterColorScheme(
					disabledColors);

			SubstanceColorSchemeBundle bundle = new SubstanceColorSchemeBundle(
					activeScheme, defaultScheme, disabledScheme);

			// bundle.registerColorScheme(disabledScheme,
			// ComponentState.DISABLED_UNSELECTED);
			//
			// bundle.registerColorScheme(defaultScheme,
			// ComponentState.DEFAULT);
			// bundle.registerColorScheme(activeScheme, ComponentState.ACTIVE);
			// bundle.registerColorScheme(disabledScheme,
			// ComponentState.DISABLED_SELECTED);
			// bundle.registerColorScheme(activeScheme,
			// ComponentState.SELECTED);
			//
			// bundle.registerColorScheme(activeScheme,
			// ComponentState.ROLLOVER_SELECTED);
			// bundle.registerColorScheme(activeScheme,
			// ComponentState.ROLLOVER_UNSELECTED);
			//
			// bundle.registerHighlightColorScheme(activeScheme,
			// ComponentState.SELECTED);
			// bundle.registerHighlightColorScheme(activeScheme,
			// ComponentState.ROLLOVER_SELECTED);
			// bundle.registerHighlightColorScheme(activeScheme,
			// ComponentState.ROLLOVER_UNSELECTED);

			this.registerDecorationAreaSchemeBundle(bundle,
					DecorationAreaType.NONE);

			this.registerAsDecorationArea(activeScheme,
					DecorationAreaType.PRIMARY_TITLE_PANE,
					DecorationAreaType.SECONDARY_TITLE_PANE,
					DecorationAreaType.HEADER, DecorationAreaType.FOOTER,
					DecorationAreaType.GENERAL, DecorationAreaType.TOOLBAR);

			setSelectedTabFadeStart(0.3);
			setSelectedTabFadeEnd(0.6);

			this.buttonShaper = new ClassicButtonShaper();
			this.gradientPainter = new SimplisticGradientPainter();
			this.decorationPainter = new ArcDecorationPainter();
			this.highlightPainter = new ClassicHighlightPainter();
			this.borderPainter = new GlassBorderPainter();
		}

		public String getDisplayName() {
			return "Custom";
		}
	}

	protected static class MyCustomLookAndFeel extends SubstanceLookAndFeel {
		public MyCustomLookAndFeel() {
			super(new CustomSkin());
		}
	}

	public static void initLaF() throws Exception {
		UIManager.setLookAndFeel(new MyCustomLookAndFeel());
		UIManager.put(SubstanceLookAndFeel.FOCUS_KIND,
				SubstanceConstants.FocusKind.NONE);
		UIManager.put(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY,
				Boolean.TRUE);
	}

	public BackgroundColorTest(String title) {
		super(title);
		initGUI();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initGUI() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JButton("Test"), BorderLayout.NORTH);

		JPanel p = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};
		JLabel label = new JLabel("Label");
		// set label new background --> doesn't work
		label.setBackground(Color.green);
		p.add(label);
		// set panel p new background --> doesn't work
		p.setBackground(Color.BLUE);
		p.setPreferredSize(new Dimension(200, 200));
		p.setBorder(BorderFactory.createLineBorder(Color.red));

		panel.add(p, BorderLayout.SOUTH);
		panel.setPreferredSize(new Dimension(600, 600));
		getContentPane().add(panel);
	}
}
