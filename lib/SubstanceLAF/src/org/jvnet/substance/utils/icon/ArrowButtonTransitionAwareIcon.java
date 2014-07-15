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
package org.jvnet.substance.utils.icon;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.lafwidget.animation.FadeState;
import org.jvnet.substance.api.*;
import org.jvnet.substance.utils.*;
import org.jvnet.substance.utils.icon.TransitionAwareIcon.Delegate;

/**
 * Transition aware implementation of arrow button icons. Used for implementing
 * icons of scroll bar buttons, combobox buttons, menus and more.
 * 
 * @author Kirill Grouchnikov
 */
@TransitionAware
public class ArrowButtonTransitionAwareIcon implements Icon {
	/**
	 * Icon cache to speed up the subsequent icon painting. The basic assumption
	 * is that the {@link #delegate} returns an icon that paints the same for
	 * the same parameters.
	 */
	private static LazyResettableHashMap<Icon> iconMap = new LazyResettableHashMap<Icon>(
			"ButtonArrowTransitionAwareIcon");

	/**
	 * Arrow icon orientation. Must be one of {@link SwingConstants#NORTH},
	 * {@link SwingConstants#SOUTH}, {@link SwingConstants#EAST} or
	 * {@link SwingConstants#WEST}.
	 */
	private int orientation;

	/**
	 * Icon width.
	 */
	protected int iconWidth;

	/**
	 * Icon height.
	 */
	protected int iconHeight;

	/**
	 * Delegate to compute the actual icons.
	 */
	protected Delegate delegate;

	/**
	 * Creates an arrow icon.
	 * 
	 * @param button
	 *            Arrow button.
	 * @param orientation
	 *            Arrow icon orientation.
	 */
	public ArrowButtonTransitionAwareIcon(final AbstractButton button,
			final int orientation) {
		this.orientation = orientation;
		this.delegate = new TransitionAwareIcon.Delegate() {
			@Override
			public Icon getColorSchemeIcon(SubstanceColorScheme scheme) {
				// System.out.println(scheme.getDisplayName());
				int fontSize = SubstanceSizeUtils.getComponentFontSize(button);
				return SubstanceImageCreator.getArrowIcon(fontSize,
						orientation, scheme);
			}
		};

		this.iconWidth = this.delegate.getColorSchemeIcon(
				SubstanceColorSchemeUtilities.getColorScheme(button,
						ComponentState.DEFAULT)).getIconWidth();
		this.iconHeight = this.delegate.getColorSchemeIcon(
				SubstanceColorSchemeUtilities.getColorScheme(button,
						ComponentState.DEFAULT)).getIconHeight();
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		this.getIconToPaint((AbstractButton) c).paintIcon(c, g, x, y);
	}

	/**
	 * Returns the icon to be painted for the current state of the button.
	 * 
	 * @param button
	 *            Arrow button.
	 * @return Icon to be painted.
	 */
	private Icon getIconToPaint(AbstractButton button) {
		ButtonModel model = button.getModel();
		ComponentState currState = ComponentState.getState(model, button,
				(button instanceof JMenu));
		ComponentState prevState = SubstanceCoreUtilities
				.getPrevComponentState(button);
		if (!currState.isKindActive(FadeKind.ENABLE))
			prevState = currState;
		float cyclePos = currState.getCyclePosition();

		// Use HIGHLIGHT for rollover menus (arrow icons)
		// and MARK for the rest
		SubstanceColorScheme currScheme = SubstanceColorSchemeUtilities
				.getColorScheme(
						button,
						(button instanceof JMenu)
								&& currState.isKindActive(FadeKind.ROLLOVER) ? ColorSchemeAssociationKind.HIGHLIGHT
								: ColorSchemeAssociationKind.MARK, currState);

		SubstanceColorScheme prevScheme = currScheme;

		FadeState fadeState = SubstanceFadeUtilities.getFadeState(button,
				FadeKind.ROLLOVER, FadeKind.SELECTION, FadeKind.PRESS,
				FadeKind.ARM);
		if (fadeState != null) {
			// Use HIGHLIGHT for rollover menus (arrow icons)
			// and MARK for the rest
			prevScheme = SubstanceColorSchemeUtilities
					.getColorScheme(
							button,
							(button instanceof JMenu)
									&& prevState
											.isKindActive(FadeKind.ROLLOVER) ? ColorSchemeAssociationKind.HIGHLIGHT
									: ColorSchemeAssociationKind.MARK,
							prevState);
			cyclePos = fadeState.getFadePosition();
			if (!fadeState.isFadingIn())
				cyclePos = 1.0f - cyclePos;
		}
		float currAlpha = SubstanceColorSchemeUtilities.getAlpha(button,
				currState);
		float prevAlpha = SubstanceColorSchemeUtilities.getAlpha(button,
				prevState);

		// if (button instanceof JMenu) {
		// System.out.println(button.getText() + ":" + prevState + "->"
		// + currState);
		// System.out.println(prevScheme.getDisplayName() + " -> "
		// + currScheme.getDisplayName());
		// }

		HashMapKey key = SubstanceCoreUtilities.getHashKey(button.getClass()
				.getName(), this.orientation, SubstanceSizeUtils
				.getComponentFontSize(button), currScheme.getDisplayName(),
				prevScheme.getDisplayName(), currAlpha, prevAlpha, cyclePos);
		if (!iconMap.containsKey(key)) {
			Icon icon = this.delegate.getColorSchemeIcon(currScheme);
			Icon prevIcon = this.delegate.getColorSchemeIcon(prevScheme);

			BufferedImage temp = SubstanceCoreUtilities.getBlankImage(icon
					.getIconWidth(), icon.getIconHeight());
			Graphics2D g2d = temp.createGraphics();

			if (currScheme == prevScheme) {
				// same scheme - can paint just the current icon, no matter
				// what the cycle position is.
				g2d.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, currAlpha));
				icon.paintIcon(button, g2d, 0, 0);
			} else {
				// make optimizations for limit values of the cycle position.
				if (cyclePos < 1.0f) {
					g2d.setComposite(AlphaComposite.SrcOver.derive(prevAlpha));
					prevIcon.paintIcon(button, g2d, 0, 0);
				}
				if (cyclePos > 0.0f) {
					g2d.setComposite(AlphaComposite.SrcOver.derive(currAlpha
							* cyclePos));
					icon.paintIcon(button, g2d, 0, 0);
				}
			}

			iconMap.put(key, new ImageIcon(temp));
			g2d.dispose();
		}

		return iconMap.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return this.iconHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return this.iconWidth;
	}
}
