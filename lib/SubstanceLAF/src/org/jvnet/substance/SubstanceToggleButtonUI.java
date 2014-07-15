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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.*;
import javax.swing.text.View;

import org.jvnet.lafwidget.animation.*;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.api.ComponentState;
import org.jvnet.substance.shaper.SubstanceButtonShaper;
import org.jvnet.substance.utils.*;
import org.jvnet.substance.utils.border.SubstanceButtonBorder;
import org.jvnet.substance.utils.icon.GlowingIcon;

/**
 * UI for toggle buttons in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceToggleButtonUI extends BasicToggleButtonUI {
	/**
	 * Painting delegate.
	 */
	private ButtonBackgroundDelegate delegate;

	/**
	 * The matching glowing icon. Is used only when
	 * {@link FadeConfigurationManager#fadeAllowed(FadeKind, Component)} returns
	 * true on {@link FadeKind#ICON_GLOW}.
	 */
	protected GlowingIcon glowingIcon;

	/**
	 * Property change listener. Listens on changes to the
	 * {@link SubstanceLookAndFeel#BUTTON_SHAPER_PROPERTY} property and
	 * {@link AbstractButton#MODEL_CHANGED_PROPERTY} property.
	 */
	protected PropertyChangeListener substancePropertyListener;

	/**
	 * Tracker for visual state transitions.
	 */
	protected ButtonVisualStateTracker substanceVisualStateTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceToggleButtonUI();
	}

	/**
	 * Simple constructor.
	 */
	public SubstanceToggleButtonUI() {
		this.delegate = new ButtonBackgroundDelegate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.swing.plaf.basic.BasicButtonUI#installDefaults(javax.swing.
	 * AbstractButton)
	 */
	@Override
	public void installDefaults(AbstractButton b) {
		super.installDefaults(b);
		if (b.getClientProperty(SubstanceButtonUI.BORDER_ORIGINAL) == null)
			b.putClientProperty(SubstanceButtonUI.BORDER_ORIGINAL, b
					.getBorder());

		if (b.getClientProperty(SubstanceButtonUI.BORDER_ORIGINAL) == null)
			b.putClientProperty(SubstanceButtonUI.BORDER_ORIGINAL, b
					.getBorder());

		trackGlowingIcon(b);

		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(b);

		if (b.getClientProperty(SubstanceButtonUI.BORDER_COMPUTED) == null) {
			b.setBorder(shaper.getButtonBorder(b));
		} else {
			Border currBorder = b.getBorder();
			if (!(currBorder instanceof SubstanceButtonBorder)) {
				b.setBorder(shaper.getButtonBorder(b));
			} else {
				SubstanceButtonBorder sbCurrBorder = (SubstanceButtonBorder) currBorder;
				if (shaper.getClass() != sbCurrBorder.getButtonShaperClass())
					b.setBorder(shaper.getButtonBorder(b));
			}
		}
		b.putClientProperty(SubstanceButtonUI.OPACITY_ORIGINAL, b.isOpaque());
		// fix for defect 140
		b.setOpaque(false);

		b.setRolloverEnabled(true);

		LookAndFeel.installProperty(b, "iconTextGap", SubstanceSizeUtils
				.getTextIconGap(SubstanceSizeUtils.getComponentFontSize(b)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.swing.plaf.basic.BasicButtonUI#uninstallDefaults(javax.swing.
	 * AbstractButton)
	 */
	@Override
	public void uninstallDefaults(AbstractButton b) {
		super.uninstallDefaults(b);

		b.setBorder((Border) b
				.getClientProperty(SubstanceButtonUI.BORDER_ORIGINAL));
		b.setOpaque((Boolean) b
				.getClientProperty(SubstanceButtonUI.OPACITY_ORIGINAL));
		b.putClientProperty(SubstanceButtonUI.OPACITY_ORIGINAL, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicButtonUI#createButtonListener(javax.swing
	 * .AbstractButton)
	 */
	@Override
	protected BasicButtonListener createButtonListener(AbstractButton b) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.swing.plaf.basic.BasicButtonUI#installListeners(javax.swing.
	 * AbstractButton)
	 */
	@Override
	protected void installListeners(final AbstractButton b) {
		super.installListeners(b);

		this.substanceVisualStateTracker = new ButtonVisualStateTracker();
		this.substanceVisualStateTracker.installListeners(b, true);
		// ButtonVisualStateTracker.track(b, true);
		// this.substanceButtonListener = new RolloverButtonListener(b);
		// b.addMouseListener(this.substanceButtonListener);
		// b.addMouseMotionListener(this.substanceButtonListener);
		// b.addFocusListener(this.substanceButtonListener);
		// b.addPropertyChangeListener(this.substanceButtonListener);
		// b.addChangeListener(this.substanceButtonListener);

		this.substancePropertyListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				// if (AbstractButton.MODEL_CHANGED_PROPERTY.equals(evt
				// .getPropertyName())) {
				// if (substanceFadeStateListener != null)
				// substanceFadeStateListener.unregisterListeners();
				// substanceFadeStateListener = new FadeStateListener(b, b
				// .getModel(), SubstanceCoreUtilities
				// .getFadeCallback(b, false));
				// substanceFadeStateListener.registerListeners();
				// }
				if (AbstractButton.ICON_CHANGED_PROPERTY.equals(evt
						.getPropertyName())) {
					trackGlowingIcon(b);
				}
			}
		};
		b.addPropertyChangeListener(this.substancePropertyListener);

		// this.substanceFadeStateListener = new FadeStateListener(b,
		// b.getModel(), SubstanceCoreUtilities.getFadeCallback(b, false));
		// this.substanceFadeStateListener.registerListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.swing.plaf.basic.BasicButtonUI#uninstallListeners(javax.swing.
	 * AbstractButton)
	 */
	@Override
	protected void uninstallListeners(AbstractButton b) {
		this.substanceVisualStateTracker.uninstallListeners(b);
		this.substanceVisualStateTracker = null;
		// ButtonVisualStateTracker.untrack(b);
		// b.removeMouseListener(this.substanceButtonListener);
		// b.removeMouseMotionListener(this.substanceButtonListener);
		// b.removeFocusListener(this.substanceButtonListener);
		// b.removePropertyChangeListener(this.substanceButtonListener);
		// b.removeChangeListener(this.substanceButtonListener);
		// this.substanceButtonListener = null;

		b.removePropertyChangeListener(this.substancePropertyListener);
		this.substancePropertyListener = null;

		// this.substanceFadeStateListener.unregisterListeners();
		// this.substanceFadeStateListener = null;

		super.uninstallListeners(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicToggleButtonUI#paint(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		// final AbstractButton b = (AbstractButton) c;
		//
		// SubstanceTextPainter textPainter = SubstanceLookAndFeel
		// .getCurrentTextPainter();
		// textPainter.init(b, null, false);
		// if (textPainter.needsBackgroundImage()) {
		// textPainter.setBackgroundFill(b, b.getParent().getBackground(),
		// true, 0, 0);
		// textPainter
		// .attachCallback(new SubstanceTextPainter.BackgroundPaintingCallback()
		// {
		// public void paintBackground(Graphics g) {
		// delegate.updateBackground(g, b);
		// };
		// });
		// } else {
		// this.delegate.updateBackground(g, b);
		// }
		//
		// FontMetrics fm = g.getFontMetrics();
		//
		// Insets i = c.getInsets();
		//
		// Rectangle viewRect = new Rectangle();
		// Rectangle iconRect = new Rectangle();
		// Rectangle textRect = new Rectangle();
		//
		// viewRect.x = i.left;
		// viewRect.y = i.top;
		// viewRect.width = b.getWidth() - (i.right + viewRect.x);
		// viewRect.height = b.getHeight() - (i.bottom + viewRect.y);
		//
		// textRect.x = textRect.y = textRect.width = textRect.height = 0;
		// iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
		//
		// Font f = c.getFont();
		// g.setFont(f);
		//
		// // layout the text and icon
		// String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(),
		// b
		// .getIcon(), b.getVerticalAlignment(), b
		// .getHorizontalAlignment(), b.getVerticalTextPosition(), b
		// .getHorizontalTextPosition(), viewRect, iconRect, textRect, b
		// .getText() == null ? 0 : b.getIconTextGap());
		//
		// View v = (View) c.getClientProperty(BasicHTML.propertyKey);
		// if (v != null) {
		// v.paint(g, textRect);
		// } else {
		// float alpha = this.paintButtonText(g, b, textRect, text);
		// Graphics2D g2d = (Graphics2D) g.create();
		// g2d.setComposite(TransitionLayout.getAlphaComposite(b, alpha, g));
		// textPainter.renderSurface(g2d);
		// g2d.dispose();
		// }
		//
		// // Paint the Icon
		// if (b.getIcon() != null) {
		// paintIcon(g, c, iconRect);
		// }
		//
		// if (b.isFocusPainted()) {
		// if (b.hasFocus()
		// || FadeTracker.getInstance().isTracked(c, FadeKind.FOCUS)) {
		// this.paintFocus(g, b, viewRect, textRect, iconRect);
		// }
		// }
		final AbstractButton b = (AbstractButton) c;

		if (b instanceof JButton) {
			JButton jb = (JButton) b;
			if (PulseTracker.isPulsating(jb)) {
				PulseTracker.update(jb);
			} else {
			}
		}

		FontMetrics fm = g.getFontMetrics();

		Insets i = c.getInsets();

		Rectangle viewRect = new Rectangle();
		Rectangle iconRect = new Rectangle();
		final Rectangle textRect = new Rectangle();

		viewRect.x = i.left;
		viewRect.y = i.top;
		viewRect.width = b.getWidth() - (i.right + viewRect.x);
		viewRect.height = b.getHeight() - (i.bottom + viewRect.y);

		textRect.x = textRect.y = textRect.width = textRect.height = 0;
		iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

		Font f = c.getFont();

		// layout the text and icon
		String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(), b
				.getIcon(), b.getVerticalAlignment(), b
				.getHorizontalAlignment(), b.getVerticalTextPosition(), b
				.getHorizontalTextPosition(), viewRect, iconRect, textRect, b
				.getText() == null ? 0 : b.getIconTextGap());

		Graphics2D g2d = (Graphics2D) g.create();

		View v = (View) c.getClientProperty(BasicHTML.propertyKey);
		g2d.setFont(f);

		this.delegate.updateBackground(g2d, b);
		if (v != null) {
			v.paint(g2d, textRect);
		} else {
			this.paintButtonText(g2d, b, textRect, text);
		}

		// Paint the Icon
		if (b.getIcon() != null) {
			paintIcon(g2d, b, iconRect);
		}

		if (b.isFocusPainted()) {
			if (b.hasFocus()
					|| FadeTracker.getInstance().isTracked(c, FadeKind.FOCUS)) {
				this.paintFocus(g2d, b, viewRect, textRect, iconRect);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.ComponentUI#getPreferredSize(javax.swing.JComponent)
	 */
	@Override
	public Dimension getPreferredSize(JComponent c) {
		AbstractButton button = (AbstractButton) c;
		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(button);

		// fix for defect 263
		Dimension superPref = super.getPreferredSize(button);
		if (superPref == null)
			return null;

		if (shaper == null)
			return superPref;

		return shaper.getPreferredSize(button, superPref);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#contains(javax.swing.JComponent, int,
	 * int)
	 */
	@Override
	public boolean contains(JComponent c, int x, int y) {
		return ButtonBackgroundDelegate.contains((JToggleButton) c, x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicButtonUI#paintFocus(java.awt.Graphics,
	 * javax.swing.AbstractButton, java.awt.Rectangle, java.awt.Rectangle,
	 * java.awt.Rectangle)
	 */
	@Override
	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect,
			Rectangle textRect, Rectangle iconRect) {
		if (!b.isFocusPainted())
			return;

		SubstanceCoreUtilities.paintFocus(g, b, b, null, textRect, 1.0f,
				SubstanceSizeUtils.getFocusRingPadding(SubstanceSizeUtils
						.getComponentFontSize(b)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicToggleButtonUI#paintIcon(java.awt.Graphics,
	 * javax.swing.AbstractButton, java.awt.Rectangle)
	 */
	@Override
	protected void paintIcon(Graphics g, AbstractButton b, Rectangle iconRect) {
		Graphics2D graphics = (Graphics2D) g.create();
		FadeTracker fadeTracker = FadeTracker.getInstance();
		Icon icon = SubstanceCoreUtilities.getIcon(b, null, this.glowingIcon,
				false);

		graphics.setComposite(TransitionLayout.getAlphaComposite(b, g));
		if (fadeTracker.isTracked(b, FadeKind.ROLLOVER)) {
			ComponentState state = ComponentState.getState(b);
			// System.out.println(state.name() + ":" + state.isRollover());
			if (state.isKindActive(FadeKind.ROLLOVER)) {// ==
				// ComponentState.ROLLOVER_UNSELECTED) {
				// Came from default state
				SubstanceCoreUtilities.getIcon(b, null, this.glowingIcon, true)
						.paintIcon(b, graphics, iconRect.x, iconRect.y);
				graphics.setComposite(TransitionLayout.getAlphaComposite(b,
						fadeTracker.getFade(b, FadeKind.ROLLOVER), g));
				icon.paintIcon(b, graphics, iconRect.x, iconRect.y);
			} else {
				// if (state == ComponentState.DEFAULT) {
				// Came from rollover state
				icon.paintIcon(b, graphics, iconRect.x, iconRect.y);
				graphics.setComposite(TransitionLayout.getAlphaComposite(b,
						fadeTracker.getFade(b, FadeKind.ROLLOVER), g));
				b.getIcon().paintIcon(b, graphics, iconRect.x, iconRect.y);
			}
		} else {
			icon.paintIcon(b, graphics, iconRect.x, iconRect.y);
		}

		graphics.dispose();
	}

	/**
	 * Paints the text.
	 * 
	 * @param g
	 *            Graphic context
	 * @param button
	 *            Button
	 * @param textRect
	 *            Text rectangle
	 * @param text
	 *            Text to paint
	 */
	protected void paintButtonText(Graphics g, AbstractButton button,
			Rectangle textRect, String text) {
		SubstanceTextUtilities.paintText(g, button, textRect, text, (button)
				.getDisplayedMnemonicIndex());
	}

	/**
	 * Tracks possible usage of glowing icon.
	 * 
	 * @param b
	 *            Button.
	 */
	protected void trackGlowingIcon(AbstractButton b) {
		Icon currIcon = b.getIcon();
		if (currIcon instanceof GlowingIcon)
			return;
		if (currIcon == null)
			return;
		this.glowingIcon = new GlowingIcon(currIcon, b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void update(Graphics g, JComponent c) {
		this.paint(g, c);
	}
}