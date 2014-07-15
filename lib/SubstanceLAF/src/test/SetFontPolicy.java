package test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.fonts.FontPolicy;
import org.jvnet.substance.fonts.FontSet;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class SetFontPolicy extends JFrame {
	private static class WrapperFontSet implements FontSet {
		private boolean isBold;

		private FontSet delegate;

		public WrapperFontSet(FontSet delegate, boolean isBold) {
			super();
			this.delegate = delegate;
			this.isBold = isBold;
		}

		private FontUIResource getWrappedFont(FontUIResource systemFont) {
			return new FontUIResource(systemFont.getFontName(),
					this.isBold ? Font.BOLD : systemFont.getStyle(), systemFont
							.getSize());
		}

		public FontUIResource getControlFont() {
			return this.getWrappedFont(this.delegate.getControlFont());
		}

		public FontUIResource getMenuFont() {
			return this.getWrappedFont(this.delegate.getMenuFont());
		}

		public FontUIResource getMessageFont() {
			return this.getWrappedFont(this.delegate.getMessageFont());
		}

		public FontUIResource getSmallFont() {
			return this.getWrappedFont(this.delegate.getSmallFont());
		}

		public FontUIResource getTitleFont() {
			return this.getWrappedFont(this.delegate.getTitleFont());
		}

		public FontUIResource getWindowTitleFont() {
			return this.getWrappedFont(this.delegate.getWindowTitleFont());
		}
	}

	/**
	 * Creates the main frame for <code>this</code> sample.
	 */
	public SetFontPolicy() {
		super("Set font policy");

		this.setLayout(new BorderLayout());

		JPanel panel = new JPanel(new FlowLayout());

		final JCheckBox isBold = new JCheckBox("is bold");
		isBold.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						// reset the base font policy to null - this
						// restores the original font policy
						SubstanceLookAndFeel.setFontPolicy(null);
						// Get the default font set
						final FontSet substanceCoreFontSet = SubstanceLookAndFeel
								.getFontPolicy().getFontSet("Substance", null);
						// Create the wrapper font set
						FontPolicy newFontPolicy = new FontPolicy() {
							public FontSet getFontSet(String lafName,
									UIDefaults table) {
								return new WrapperFontSet(substanceCoreFontSet,
										isBold.isSelected());
							}
						};

						// set the new font policy
						SubstanceLookAndFeel.setFontPolicy(newFontPolicy);

					}
				});
			}
		});
		panel.add(isBold);

		panel.add(new JButton("button"));
		panel.add(new JComboBox(new Object[] { "item1", "item2" }));

		this.add(panel, BorderLayout.CENTER);

		this.setSize(400, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SetFontPolicy().setVisible(true);
			}
		});
	}
}
