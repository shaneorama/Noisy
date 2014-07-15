/*
 * Copyright (c) 2005-2009 Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Substance Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.jvnet.substance;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumSet;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;

import org.jvnet.lafwidget.animation.*;
import org.jvnet.substance.api.*;
import org.jvnet.substance.api.SubstanceConstants.Side;
import org.jvnet.substance.utils.*;
import org.jvnet.substance.utils.border.SubstanceBorder;
import org.jvnet.substance.utils.icon.TransitionAwareIcon;

/**
 * UI for spinners in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceSpinnerUI extends BasicSpinnerUI {
	/**
	 * Tracks changes to editor, removing the border as necessary.
	 */
	protected PropertyChangeListener substancePropertyChangeListener;

	/**
	 * Listener for fade animations.
	 */
	protected FadeStateListener substanceFadeStateListener;

	/**
	 * The next (increment) button.
	 */
	protected SubstanceSpinnerButton nextButton;

	/**
	 * The previous (decrement) button.
	 */
	protected SubstanceSpinnerButton prevButton;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceSpinnerUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSpinnerUI#createNextButton()
	 */
	@Override
	protected Component createNextButton() {
		this.nextButton = new SubstanceSpinnerButton(this.spinner,
				SwingConstants.NORTH);
		this.nextButton.setFont(this.spinner.getFont());
		this.nextButton.setName("Spinner.nextButton");

		Icon icon = new TransitionAwareIcon(this.nextButton,
				new TransitionAwareIcon.Delegate() {
					public Icon getColorSchemeIcon(SubstanceColorScheme scheme) {
						int fontSize = SubstanceSizeUtils
								.getComponentFontSize(spinner);
						return SubstanceImageCreator.getArrowIcon(
								SubstanceSizeUtils
										.getSpinnerArrowIconWidth(fontSize),
								SubstanceSizeUtils
										.getSpinnerArrowIconHeight(fontSize),
								SubstanceSizeUtils
										.getArrowStrokeWidth(fontSize),
								SwingConstants.NORTH, scheme);
					}
				}, "substance.spinner.nextButton");
		this.nextButton.setIcon(icon);

		int spinnerButtonSize = SubstanceSizeUtils
				.getScrollBarWidth(SubstanceSizeUtils
						.getComponentFontSize(spinner));
		this.nextButton.setPreferredSize(new Dimension(spinnerButtonSize,
				spinnerButtonSize));
		this.nextButton.setMinimumSize(new Dimension(5, 5));

		// Set<Side> openSides = EnumSet.of(Side.BOTTOM, Side.TOP, Side.RIGHT);
		this.nextButton.putClientProperty(
				SubstanceLookAndFeel.BUTTON_OPEN_SIDE_PROPERTY, EnumSet
						.allOf(Side.class));

		this.installNextButtonListeners(this.nextButton);
		return this.nextButton;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSpinnerUI#createPreviousButton()
	 */
	@Override
	protected Component createPreviousButton() {
		this.prevButton = new SubstanceSpinnerButton(this.spinner,
				SwingConstants.SOUTH);
		this.prevButton.setFont(this.spinner.getFont());
		this.prevButton.setName("Spinner.previousButton");

		Icon icon = new TransitionAwareIcon(this.prevButton,
				new TransitionAwareIcon.Delegate() {
					public Icon getColorSchemeIcon(SubstanceColorScheme scheme) {
						// System.out.println(spinner.getFont().getSize());
						int fontSize = SubstanceSizeUtils
								.getComponentFontSize(spinner);
						float spinnerArrowIconHeight = SubstanceSizeUtils
								.getSpinnerArrowIconHeight(fontSize);
						// System.out.println("OLD height : "
						// + spinnerArrowIconHeight);
						return SubstanceImageCreator.getArrowIcon(
								SubstanceSizeUtils
										.getSpinnerArrowIconWidth(fontSize),
								spinnerArrowIconHeight, SubstanceSizeUtils
										.getArrowStrokeWidth(fontSize),
								SwingConstants.SOUTH, scheme);
					}
				}, "substance.spinner.prevButton");
		this.prevButton.setIcon(icon);

		int spinnerButtonSize = SubstanceSizeUtils
				.getScrollBarWidth(SubstanceSizeUtils
						.getComponentFontSize(this.prevButton));
		this.prevButton.setPreferredSize(new Dimension(spinnerButtonSize,
				spinnerButtonSize));
		this.prevButton.setMinimumSize(new Dimension(5, 5));

		// Set<Side> openSides = EnumSet.of(Side.BOTTOM, Side.TOP, Side.RIGHT);
		this.prevButton.putClientProperty(
				SubstanceLookAndFeel.BUTTON_OPEN_SIDE_PROPERTY, EnumSet
						.allOf(Side.class));

		this.installPreviousButtonListeners(this.prevButton);
		return this.prevButton;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSpinnerUI#installDefaults()
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();
		JComponent editor = this.spinner.getEditor();
		if ((editor != null) && (editor instanceof JSpinner.DefaultEditor)) {
			JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
			if (tf != null) {
				int fontSize = SubstanceSizeUtils
						.getComponentFontSize(this.spinner);
				Insets ins = SubstanceSizeUtils
						.getSpinnerTextBorderInsets(fontSize);
				tf.setBorder(new EmptyBorder(ins.top, ins.left, ins.bottom,
						ins.right));
				tf.setFont(spinner.getFont());
			}
		}

		Border b = this.spinner.getBorder();
		if (b == null || b instanceof UIResource) {
			this.spinner.setBorder(new SubstanceBorder(SubstanceSizeUtils
					.getSpinnerBorderInsets(SubstanceSizeUtils
							.getComponentFontSize(this.spinner))));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSpinnerUI#installListeners()
	 */
	@Override
	protected void installListeners() {
		super.installListeners();
		this.substancePropertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("editor".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (substanceFadeStateListener != null) {
								substanceFadeStateListener
										.unregisterListeners();
								substanceFadeStateListener = null;
							}
							if (spinner == null)
								return;
							JComponent editor = spinner.getEditor();
							if ((editor != null)
									&& (editor instanceof JSpinner.DefaultEditor)) {
								JTextField tf = ((JSpinner.DefaultEditor) editor)
										.getTextField();
								if (tf != null) {
									Insets ins = SubstanceSizeUtils
											.getSpinnerTextBorderInsets(SubstanceSizeUtils
													.getComponentFontSize(spinner));
									tf.setBorder(new EmptyBorder(ins.top,
											ins.left, ins.bottom, ins.right));
									substanceFadeStateListener = new FadeStateListener(
											tf, null, new FadeTrackerAdapter() {
												@Override
												public void fadeEnded(
														FadeKind fadeKind) {
													if (spinner != null)
														spinner.repaint();
												}

												@Override
												public void fadePerformed(
														FadeKind fadeKind,
														float fadeCycle10) {
													if (spinner != null)
														spinner.repaint();
												}
											});
									substanceFadeStateListener
											.registerListeners();
								}
							}
						}
					});
				}

				if ("font".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (spinner != null) {
								spinner.updateUI();
							}
						}
					});
				}

				if ("background".equals(evt.getPropertyName())) {
					JComponent editor = spinner.getEditor();
					if ((editor != null)
							&& (editor instanceof JSpinner.DefaultEditor)) {
						JTextField tf = ((JSpinner.DefaultEditor) editor)
								.getTextField();
						if (tf != null) {
							// Use SubstanceColorResource to distingish between
							// color set by application and color set
							// (propagated)
							// by Substance. In the second case we can replace
							// that color (even though it's not a UIResource).
							Color tfBackground = tf.getBackground();
							boolean canReplace = SubstanceCoreUtilities
									.canReplaceChildBackgroundColor(tfBackground);
							// fix for issue 387 - if spinner background
							// is null, do nothing
							if (spinner.getBackground() == null)
								canReplace = false;
							if (canReplace) {
								tf.setBackground(new SubstanceColorResource(
										spinner.getBackground()));
							}
						}
					}
				}
			}
		};
		this.spinner
				.addPropertyChangeListener(this.substancePropertyChangeListener);

		JComponent editor = spinner.getEditor();
		if ((editor != null) && (editor instanceof JSpinner.DefaultEditor)) {
			JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
			this.substanceFadeStateListener = new FadeStateListener(tf, null,
					new FadeTrackerAdapter() {
						@Override
						public void fadeEnded(FadeKind fadeKind) {
							if (spinner != null)
								spinner.repaint();
						}

						@Override
						public void fadePerformed(FadeKind fadeKind,
								float fadeCycle10) {
							if (spinner != null)
								spinner.repaint();
						}
					});
			this.substanceFadeStateListener.registerListeners();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSpinnerUI#uninstallListeners()
	 */
	@Override
	protected void uninstallListeners() {
		// listener can be null is there is a custom editor installed
		// on the spinner.
		if (this.substanceFadeStateListener != null) {
			this.substanceFadeStateListener.unregisterListeners();
			this.substanceFadeStateListener = null;
		}

		this.spinner
				.removePropertyChangeListener(this.substancePropertyChangeListener);
		this.substancePropertyChangeListener = null;

		super.uninstallListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#paint(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
		if (hasFocus(this.spinner)
				|| FadeTracker.getInstance().isTracked(this.spinner,
						FadeKind.FOCUS)) {
			this.paintFocus(g, this.spinner.getEditor().getBounds());
		}
		if (this.spinner.isEnabled()) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(Color.red);
			SubstanceColorScheme borderColorScheme = SubstanceColorSchemeUtilities
					.getColorScheme(c, ColorSchemeAssociationKind.BORDER,
							ComponentState.DEFAULT);
			if (this.spinner.getComponentOrientation().isLeftToRight()) {
				g2d.translate(this.spinner.getEditor().getX()
						+ this.spinner.getEditor().getWidth(), -5);
				SubstanceImageCreator.paintSimpleBorder(this.spinner, g2d,
						2 * nextButton.getWidth(),
						this.spinner.getHeight() + 10, borderColorScheme,
						borderColorScheme, 0.0f);
			} else {
				g2d.translate(-5, -5);
				SubstanceImageCreator.paintSimpleBorder(this.spinner, g2d,
						5 + this.spinner.getEditor().getX(), this.spinner
								.getHeight() + 10, borderColorScheme,
						borderColorScheme, 0.0f);
			}
			g2d.dispose();
		}

	}

	/**
	 * Checks if a component or any of its children have focus.
	 * 
	 * @param comp
	 *            Component.
	 * @return <code>true</code> if the component of any of its children have
	 *         focus, <code>false</code> otherwise.
	 */
	private static boolean hasFocus(Component comp) {
		if (comp.hasFocus())
			return true;
		if (comp instanceof Container) {
			Container cont = (Container) comp;
			for (int i = 0; i < cont.getComponentCount(); i++) {
				Component child = cont.getComponent(i);
				if (hasFocus(child))
					return true;
			}
		}
		return false;
	}

	/**
	 * Paints the focus indication.
	 * 
	 * @param g
	 *            Graphics.
	 * @param bounds
	 *            Bounds for text.
	 */
	protected void paintFocus(Graphics g, Rectangle bounds) {
		// JComponent editor = spinner.getEditor();
		// if ((editor != null) && (editor instanceof JSpinner.DefaultEditor)) {
		// JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
		// if (tf != null) {
		// SubstanceCoreUtilities.paintFocus(g, this.spinner, tf, null,
		// bounds, 0.4f, 2 + SubstanceSizeUtils
		// .getExtraPadding(SubstanceSizeUtils
		// .getComponentFontSize(spinner)));
		// }
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.ComponentUI#getPreferredSize(javax.swing.JComponent)
	 */
	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension nextD = this.nextButton.getPreferredSize();
		Dimension previousD = this.prevButton.getPreferredSize();
		Dimension editorD = spinner.getEditor().getPreferredSize();

		Dimension size = new Dimension(editorD.width, editorD.height);
		size.width += Math.max(nextD.width, previousD.width);
		Insets insets = this.spinner.getInsets();
		size.width += insets.left + insets.right;
		size.height += insets.top + insets.bottom;
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void update(Graphics g, JComponent c) {
		SubstanceTextUtilities.paintTextCompBackground(g, c);
		this.paint(g, c);
	}

	@Override
	protected LayoutManager createLayout() {
		return new SpinnerLayoutManager();
	}

	/**
	 * Layout manager for the spinner.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class SpinnerLayoutManager implements LayoutManager {
		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public Dimension minimumLayoutSize(Container parent) {
			return this.preferredLayoutSize(parent);
		}

		public Dimension preferredLayoutSize(Container parent) {
			Dimension nextD = nextButton.getPreferredSize();
			Dimension previousD = prevButton.getPreferredSize();
			Dimension editorD = spinner.getEditor().getPreferredSize();

			/*
			 * Force the editors height to be a multiple of 2
			 */
			editorD.height = ((editorD.height + 1) / 2) * 2;

			Dimension size = new Dimension(editorD.width, editorD.height);
			size.width += Math.max(nextD.width, previousD.width);
			Insets insets = parent.getInsets();
			size.width += insets.left + insets.right;
			size.height += insets.top + insets.bottom;

			Insets buttonInsets = SubstanceSizeUtils
					.getSpinnerArrowButtonInsets(SubstanceSizeUtils
							.getComponentFontSize(spinner));
			size.width += (buttonInsets.left + buttonInsets.right);

			return size;
		}

		public void layoutContainer(Container parent) {
			int width = parent.getWidth();
			int height = parent.getHeight();

			Insets insets = parent.getInsets();
			Dimension nextD = nextButton.getPreferredSize();
			Dimension previousD = prevButton.getPreferredSize();
			int buttonsWidth = Math.max(nextD.width, previousD.width);
			int editorHeight = height - (insets.top + insets.bottom);

			Insets buttonInsets = SubstanceSizeUtils
					.getSpinnerArrowButtonInsets(SubstanceSizeUtils
							.getComponentFontSize(spinner));

			/*
			 * Deal with the spinner's componentOrientation property.
			 */
			int editorX, editorWidth, buttonsX;
			if (parent.getComponentOrientation().isLeftToRight()) {
				editorX = insets.left;
				editorWidth = width - insets.left - buttonsWidth
						- buttonInsets.right - buttonInsets.left;
				buttonsX = width - buttonsWidth - buttonInsets.right;
			} else {
				buttonsX = buttonInsets.left;
				editorX = buttonsX + buttonsWidth + buttonInsets.left;
				editorWidth = width - buttonInsets.left - buttonsWidth
						- insets.right - buttonInsets.left;
			}

			int nextY = buttonInsets.top;
			int nextHeight = (height / 2) + (height % 2) - nextY;
			int previousY = buttonInsets.top + nextHeight;
			int previousHeight = height - previousY - buttonInsets.bottom;

			spinner.getEditor().setBounds(editorX, insets.top, editorWidth,
					editorHeight);
			nextButton.setBounds(buttonsX, nextY, buttonsWidth, nextHeight);
			prevButton.setBounds(buttonsX, previousY, buttonsWidth,
					previousHeight);
			// System.out.println("next : " + nextButton.getBounds());
			// System.out.println("prev : " + prevButton.getBounds());
		}
	}
}
