package test.check.statusbar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.fonts.FontPolicy;
import org.jvnet.substance.fonts.FontSet;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class FontSizePanel {
	private static class WrapperFontSet implements FontSet {
		private int extra;

		private FontSet delegate;

		public WrapperFontSet(FontSet delegate, int extra) {
			super();
			this.delegate = delegate;
			this.extra = extra;
		}

		private FontUIResource getWrappedFont(FontUIResource systemFont) {
			return new FontUIResource(systemFont.getFontName(), systemFont
					.getStyle(), systemFont.getSize() + extra);
		}

		public FontUIResource getControlFont() {
			return getWrappedFont(delegate.getControlFont());
		}

		public FontUIResource getMenuFont() {
			return getWrappedFont(delegate.getMenuFont());
		}

		public FontUIResource getMessageFont() {
			return getWrappedFont(delegate.getMessageFont());
		}

		public FontUIResource getSmallFont() {
			return getWrappedFont(delegate.getSmallFont());
		}

		public FontUIResource getTitleFont() {
			return getWrappedFont(delegate.getTitleFont());
		}

		public FontUIResource getWindowTitleFont() {
			// FontUIResource f = this.getWrappedFont(this.delegate
			// .getWindowTitleFont());
			// return new FontUIResource(f.deriveFont(Font.BOLD, f.getSize() +
			// 1));
			return getWrappedFont(delegate.getWindowTitleFont());
		}
	}

	public static JPanel getPanel() {
		FormLayout lm = new FormLayout(
				"fill:pref, 2dlu, fill:pref, 0dlu, fill:min:grow, 0dlu, fill:pref",
				"");
		DefaultFormBuilder builder = new DefaultFormBuilder(lm);
		builder.setBorder(new EmptyBorder(0, 0, 0, 0));

		final JLabel fontSizeLabel = new JLabel();
		fontSizeLabel.setText(SubstanceLookAndFeel.getFontPolicy().getFontSet(
				"Substance", null).getControlFont().getSize()
				+ " pt.");
		builder.append(fontSizeLabel);
		// fontSizeLabel.add(Box.createHorizontalStrut(5));

		Icon zoomOutIcon = new ImageIcon(Thread.currentThread()
				.getContextClassLoader().getResource(
						"test/check/icons/16/zoom_out.png"));
		JLabel zoomOutLabel = new JLabel(zoomOutIcon);
		zoomOutLabel.putClientProperty(
				SubstanceLookAndFeel.USE_THEMED_DEFAULT_ICONS, Boolean.TRUE);
		builder.append(zoomOutLabel);

		final JSlider fontSizeSlider = new JSlider(0, 10, 0);
		fontSizeSlider.setFocusable(false);
		// fontSizeSlider.setOpaque(false);
		fontSizeSlider
				.setToolTipText("Controls the global font set size. Resets Substance as the current LAF.");
		fontSizeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// if the value is adjusting - ignore. This is done
				// to make CPU usage better.
				if (!fontSizeSlider.getModel().getValueIsAdjusting()) {
					final int newValue = fontSizeSlider.getValue();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							SubstanceLookAndFeel.setFontPolicy(null);
							final FontSet substanceCoreFontSet = SubstanceLookAndFeel
									.getFontPolicy().getFontSet("Substance",
											null);
							FontPolicy newFontPolicy = new FontPolicy() {
								public FontSet getFontSet(String lafName,
										UIDefaults table) {
									return new WrapperFontSet(
											substanceCoreFontSet, newValue);
								}
							};

							SubstanceLookAndFeel.setFontPolicy(newFontPolicy);

							fontSizeLabel.setText(SubstanceLookAndFeel
									.getFontPolicy().getFontSet("Substance",
											null).getControlFont().getSize()
									+ " pt.");
						}
					});
				}
			}
		});
		builder.append(fontSizeSlider);

		Icon zoomInIcon = new ImageIcon(Thread.currentThread()
				.getContextClassLoader().getResource(
						"test/check/icons/16/zoom_in.png"));
		JLabel zoomInLabel = new JLabel(zoomInIcon);
		zoomInLabel.putClientProperty(
				SubstanceLookAndFeel.USE_THEMED_DEFAULT_ICONS, Boolean.TRUE);
		builder.append(zoomInLabel);

		JPanel result = builder.getPanel();
		// result.setOpaque(false);
		return result;
	}
}
