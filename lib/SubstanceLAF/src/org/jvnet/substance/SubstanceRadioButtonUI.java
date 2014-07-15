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
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.*;
import javax.swing.text.View;

import org.jvnet.lafwidget.animation.*;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.api.*;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;
import org.jvnet.substance.painter.utils.BackgroundPaintingUtils;
import org.jvnet.substance.utils.*;

/**
 * UI for radio buttons in <b>Substance </b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceRadioButtonUI extends BasicRadioButtonUI {
	/**
	 * Property change listener. Listens on changes to
	 * {@link AbstractButton#MODEL_CHANGED_PROPERTY} property.
	 */
	protected PropertyChangeListener substancePropertyListener;

	/**
	 * Associated toggle button.
	 */
	protected JToggleButton button;

	/**
	 * Icons for all component states
	 */
	private static LazyResettableHashMap<Icon> icons = new LazyResettableHashMap<Icon>(
			"SubstanceRadioButtonUI");

	/**
	 * Listener for fade animations.
	 */
	protected FadeStateListener substanceFadeStateListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.swing.plaf.basic.BasicButtonUI#installListeners(javax.swing.
	 * AbstractButton)
	 */
	@Override
	protected void installListeners(final AbstractButton b) {
		super.installListeners(b);

		substanceFadeStateListener = new FadeStateListener(b, b.getModel(),
				SubstanceCoreUtilities.getFadeCallback(b, false));
		substanceFadeStateListener.registerListeners();

		substancePropertyListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (AbstractButton.MODEL_CHANGED_PROPERTY.equals(evt
						.getPropertyName())) {
					if (substanceFadeStateListener != null)
						substanceFadeStateListener.unregisterListeners();
					substanceFadeStateListener = new FadeStateListener(b, b
							.getModel(), SubstanceCoreUtilities
							.getFadeCallback(b, false));
					substanceFadeStateListener.registerListeners();
				}
				// if ("opaque".equals(evt.getPropertyName())) {
				// if (!Boolean.TRUE.equals(b
				// .getClientProperty(SubstanceButtonUI.LOCK_OPACITY))) {
				// b.putClientProperty(SubstanceButtonUI.OPACITY_ORIGINAL,
				// evt.getNewValue());
				// // System.out
				// // .println("PCL: "
				// // + b.getText()
				// // + "->"
				// // + b
				// // .getClientProperty(SubstanceButtonUI.OPACITY_ORIGINAL
				// // ));
				// }
				// }
				if ("font".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							b.updateUI();
						}
					});
				}
			}
		};
		b.addPropertyChangeListener(substancePropertyListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicRadioButtonUI#installDefaults(javax.swing
	 * .AbstractButton)
	 */
	@Override
	protected void installDefaults(AbstractButton b) {
		super.installDefaults(b);
		Border border = b.getBorder();
		if (border == null || border instanceof UIResource) {
			b.setBorder(SubstanceSizeUtils
					.getRadioButtonBorder(SubstanceSizeUtils
							.getComponentFontSize(b)));
		}

		button.setRolloverEnabled(true);

		LookAndFeel.installProperty(b, "iconTextGap", SubstanceSizeUtils
				.getTextIconGap(SubstanceSizeUtils.getComponentFontSize(b)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.swing.plaf.basic.BasicButtonUI#uninstallListeners(javax.swing.
	 * AbstractButton)
	 */
	@Override
	protected void uninstallListeners(AbstractButton b) {
		substanceFadeStateListener.unregisterListeners();
		substanceFadeStateListener = null;

		b.removePropertyChangeListener(substancePropertyListener);
		substancePropertyListener = null;

		super.uninstallListeners(b);
	}

	/**
	 * Returns the icon that matches the current and previous states of the
	 * radio button.
	 * 
	 * @param button
	 *            Button (should be {@link JRadioButton}).
	 * @param currState
	 *            Current state of the checkbox.
	 * @param prevState
	 *            Previous state of the checkbox.
	 * @return Matching icon.
	 */
	private static Icon getIcon(JToggleButton button, ComponentState currState,
			ComponentState prevState) {
		float visibility = currState.isKindActive(FadeKind.SELECTION) ? 10 : 0;

		SubstanceColorScheme currFillColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.FILL,
						currState);
		SubstanceColorScheme prevFillColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.FILL,
						prevState);

		SubstanceColorScheme currMarkColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.MARK,
						currState);
		SubstanceColorScheme prevMarkColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.MARK,
						prevState);

		SubstanceColorScheme currBorderColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.BORDER,
						currState);
		SubstanceColorScheme prevBorderColorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.BORDER,
						prevState);

		float cyclePos = 0;

		FadeState fadeState = SubstanceFadeUtilities.getFadeState(button,
				FadeKind.SELECTION, FadeKind.ROLLOVER, FadeKind.PRESS);
		if (fadeState != null) {
			cyclePos = fadeState.getFadePosition();
			if (fadeState.isFadingIn())
				cyclePos = 1.0f - cyclePos;
			if (fadeState.fadeKind == FadeKind.SELECTION) {
				visibility = fadeState.getFadePosition();
			}
		}

		int fontSize = SubstanceSizeUtils.getComponentFontSize(button);
		int checkMarkSize = SubstanceSizeUtils.getRadioButtonMarkSize(fontSize);

		SubstanceGradientPainter fillPainter = SubstanceCoreUtilities
				.getGradientPainter(button);
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(button);

		HashMapKey key = SubstanceCoreUtilities.getHashKey(fontSize,
				checkMarkSize, currState.name(), currState.name(), fillPainter
						.getDisplayName(), borderPainter.getDisplayName(),
				currFillColorScheme.getDisplayName(), prevFillColorScheme
						.getDisplayName(),
				currMarkColorScheme.getDisplayName(), prevMarkColorScheme
						.getDisplayName(), currBorderColorScheme
						.getDisplayName(), prevBorderColorScheme
						.getDisplayName(), cyclePos, visibility);

		Icon result = SubstanceRadioButtonUI.icons.get(key);
		if (result != null)
			return result;
		result = new ImageIcon(SubstanceImageCreator.getRadioButton(button,
				fillPainter, borderPainter, checkMarkSize, currState,
				prevState, 0, currFillColorScheme, prevFillColorScheme,
				currMarkColorScheme, prevMarkColorScheme,
				currBorderColorScheme, prevBorderColorScheme, cyclePos,
				visibility / 10.f));
		SubstanceRadioButtonUI.icons.put(key, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceRadioButtonUI((JToggleButton) comp);
	}

	/**
	 * Simple constructor.
	 * 
	 * @param button
	 *            Associated radio button.
	 */
	public SubstanceRadioButtonUI(JToggleButton button) {
		this.button = button;
		button.setRolloverEnabled(true);
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
		return new RolloverButtonListener(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicRadioButtonUI#getDefaultIcon()
	 */
	@Override
	public Icon getDefaultIcon() {
		ComponentState currState = ComponentState.getState(button);
		ComponentState prevState = SubstanceCoreUtilities
				.getPrevComponentState(button);
		return SubstanceRadioButtonUI.getIcon(button, currState, prevState);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		AbstractButton b = (AbstractButton) c;

		// boolean isOpaque = b.isOpaque();
		// b.putClientProperty(SubstanceButtonUI.LOCK_OPACITY, Boolean.TRUE);
		// b.setOpaque(false);

		if (// isOpaque ||
		TransitionLayout.isOpaque(c)) {
			BackgroundPaintingUtils.update(g, c, false);
		}

		// b.setOpaque(isOpaque);

		// b.putClientProperty(SubstanceButtonUI.LOCK_OPACITY, null);

		FontMetrics fm = g.getFontMetrics();

		Insets i = b.getInsets();

		Rectangle viewRect = new Rectangle();
		Rectangle iconRect = new Rectangle();
		final Rectangle textRect = new Rectangle();

		viewRect.x = i.left;
		viewRect.y = i.top;
		viewRect.width = b.getWidth() - (i.right + viewRect.x);
		viewRect.height = b.getHeight() - (i.bottom + viewRect.y);

		textRect.x = textRect.y = textRect.width = textRect.height = 0;
		iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

		Font f = b.getFont();
		g.setFont(f);

		Icon icon = SubstanceCoreUtilities.getIcon(b, this.getDefaultIcon(),
				null, false);

		// layout the text and icon
		String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(),
				icon, b.getVerticalAlignment(), b.getHorizontalAlignment(), b
						.getVerticalTextPosition(), b
						.getHorizontalTextPosition(), viewRect, iconRect,
				textRect, b.getText() == null ? 0 : b.getIconTextGap());

		Graphics2D g2d = (Graphics2D) g.create();
		if (text != null && !text.equals("")) {
			final View v = (View) b.getClientProperty(BasicHTML.propertyKey);
			if (v != null) {
				v.paint(g2d, textRect);
			} else {
				this.paintButtonText(g2d, b, textRect, text);
			}
		}

		// Paint the Icon
		if (icon != null) {
			icon.paintIcon(c, g2d, iconRect.x, iconRect.y);
		}

		if (b.isFocusPainted()) {
			if (b.hasFocus()
					|| FadeTracker.getInstance().isTracked(c, FadeKind.FOCUS)) {
				// make sure that the focus ring is not clipped
				int focusRingPadding = SubstanceSizeUtils
						.getFocusRingPadding(SubstanceSizeUtils
								.getComponentFontSize(button)) / 2;
				SubstanceCoreUtilities.paintFocus(g2d, button, button, null,
						textRect, 1.0f, focusRingPadding);
			}
		}
		// g2d.setColor(Color.red);
		// g2d.draw(iconRect);
		// g2d.draw(viewRect);
		// g2d.draw(textRect);
		// g2d.setColor(Color.blue);
		// g2d.drawRect(0, 0, button.getWidth() - 1, button.getHeight() - 1);

		g2d.dispose();
	}

	/**
	 * Returns memory usage string.
	 * 
	 * @return Memory usage string.
	 */
	public static String getMemoryUsage() {
		StringBuffer sb = new StringBuffer();
		sb.append("SubstanceRadioButtonUI: \n");
		sb.append("\t" + SubstanceRadioButtonUI.icons.size() + " icons");
		return sb.toString();
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
		SubstanceTextUtilities.paintText(g, button, textRect, text, button
				.getDisplayedMnemonicIndex());
	}
}
