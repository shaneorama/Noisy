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
package org.jvnet.substance.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.Set;

import javax.swing.AbstractButton;

import org.jvnet.lafwidget.animation.*;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.*;
import org.jvnet.substance.api.SubstanceConstants.Side;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;

/**
 * Delegate class for painting backgrounds of buttons in <b>Substance </b> look
 * and feel. This class is <b>for internal use only</b>.
 * 
 * @author Kirill Grouchnikov
 */
public class PairwiseButtonBackgroundDelegate {
	/**
	 * Cache for background images for pairwise backgrounds. Each time
	 * {@link #getPairwiseBackground(AbstractButton, int, int, Side)} is called,
	 * it checks <code>this</code> map to see if it already contains such
	 * background. If so, the background from the map is returned.
	 */
	private static LazyResettableHashMap<BufferedImage> pairwiseBackgrounds = new LazyResettableHashMap<BufferedImage>(
			"PairwiseButtonBackgroundDelegate");

	/**
	 * Paints background image for the specified button in button pair (such as
	 * scrollbar arrows, for example).
	 * 
	 * @param g
	 *            Graphics context.
	 * @param button
	 *            Button.
	 * @param painter
	 *            Gradient painter.
	 * @param width
	 *            Button width.
	 * @param height
	 *            Button height.
	 * @param side
	 *            Button orientation.
	 * @param toIgnoreOpenSides
	 *            If <code>true</code>, the open side setting (controlled by the
	 *            {@link SubstanceLookAndFeel#BUTTON_OPEN_SIDE_PROPERTY} is
	 *            ignored.
	 */
	public static void updatePairwiseBackground(Graphics g,
			AbstractButton button, int width, int height,
			SubstanceConstants.Side side, boolean toIgnoreOpenSides) {
		if (SubstanceCoreUtilities.isButtonNeverPainted(button))
			return;

		BufferedImage resultNonFlat = null;
		ComponentState state = ComponentState.getState(button);
		ComponentState prevState = SubstanceCoreUtilities
				.getPrevComponentState(button);

		float cyclePos = state.getCyclePosition();
		SubstanceColorScheme colorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, state);
		SubstanceColorScheme colorScheme2 = colorScheme;
		SubstanceColorScheme borderScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.BORDER,
						state);
		SubstanceColorScheme borderScheme2 = borderScheme;

		FadeTracker fadeTracker = FadeTracker.getInstance();
		FadeState fadeState = fadeTracker.getFadeState(button,
				FadeKind.ROLLOVER);
		if (fadeState != null) {
			colorScheme = SubstanceColorSchemeUtilities.getColorScheme(button,
					prevState);
			colorScheme2 = SubstanceColorSchemeUtilities.getColorScheme(button,
					state);
			borderScheme = SubstanceColorSchemeUtilities.getColorScheme(button,
					ColorSchemeAssociationKind.BORDER, prevState);
			borderScheme2 = SubstanceColorSchemeUtilities.getColorScheme(
					button, ColorSchemeAssociationKind.BORDER, state);
			cyclePos = fadeState.getFadePosition();
			if (!fadeState.isFadingIn())
				cyclePos = 1.0f - cyclePos;

			BufferedImage imageDefault = getPairwiseFullAlphaBackground(
					button,
					SubstanceImageCreator.SimplisticSoftBorderReverseGradientPainter.INSTANCE,
					width, height, side, cyclePos, colorScheme, colorScheme2,
					borderScheme, borderScheme2, toIgnoreOpenSides);
			if (imageDefault == null)
				return;
			resultNonFlat = imageDefault;
		} else {
			resultNonFlat = getPairwiseFullAlphaBackground(
					button,
					SubstanceImageCreator.SimplisticSoftBorderReverseGradientPainter.INSTANCE,
					width, height, side, 0, colorScheme, colorScheme2,
					borderScheme, borderScheme2, toIgnoreOpenSides);
		}

		boolean isFlat = SubstanceCoreUtilities.hasFlatAppearance(button);
		boolean isSpecial = isFlat || !state.isKindActive(FadeKind.ENABLE);
		float extraAlpha = 1.0f;

		if (isSpecial) {
			if (isFlat) {
				// Special handling of flat buttons
				if (FadeTracker.getInstance().isTracked(button,
						FadeKind.ROLLOVER)
						&& !state.isKindActive(FadeKind.SELECTION)
						&& state.isKindActive(FadeKind.ENABLE)) {
					extraAlpha = FadeTracker.getInstance().getFade(button,
							FadeKind.ROLLOVER);
				} else {
					if (state == ComponentState.DEFAULT) {
						// flat button in default state and not
						// participating in rollover animation
						extraAlpha = 0.0f;
					}
				}
				if (state == ComponentState.DISABLED_UNSELECTED)
					extraAlpha = 0.0f;
			} else {
				if (!state.isKindActive(FadeKind.ENABLE)) {
					extraAlpha = SubstanceColorSchemeUtilities.getAlpha(button,
							state);
				}
			}
		}
		if (extraAlpha > 0.0f) {
			Graphics2D graphics = (Graphics2D) g.create();
			graphics.setComposite(TransitionLayout.getAlphaComposite(button,
					extraAlpha, g));
			graphics.drawImage(resultNonFlat, 0, 0, null);
			graphics.dispose();
		}
	}

	/**
	 * Retrieves background image for the specified button in button pair (such
	 * as scrollbar arrows, for example).
	 * 
	 * @param button
	 *            Button.
	 * @param kind
	 *            Color scheme kind.
	 * @param painter
	 *            Gradient painter.
	 * @param width
	 *            Button width.
	 * @param height
	 *            Button height.
	 * @param side
	 *            Button orientation.
	 * @param cyclePos
	 *            Cycle position.
	 * @param colorScheme
	 *            The first color scheme.
	 * @param colorScheme2
	 *            The second color scheme.
	 * @param borderScheme
	 *            The first border color scheme.
	 * @param borderScheme2
	 *            The second border color scheme.
	 * @param graphicsComposite
	 *            Composite to apply before painting the button.
	 * @param toIgnoreOpenSides
	 *            If <code>true</code>, the open side setting (controlled by the
	 *            {@link SubstanceLookAndFeel#BUTTON_OPEN_SIDE_PROPERTY} is
	 *            ignored.
	 * @return Button background image.
	 */
	private static BufferedImage getPairwiseFullAlphaBackground(
			AbstractButton button, SubstanceGradientPainter painter, int width,
			int height, SubstanceConstants.Side side, float cyclePos,
			SubstanceColorScheme colorScheme,
			SubstanceColorScheme colorScheme2,
			SubstanceColorScheme borderScheme,
			SubstanceColorScheme borderScheme2, boolean toIgnoreOpenSides) {
		if (SubstanceCoreUtilities.isButtonNeverPainted(button))
			return null;
		Set<Side> openSides = toIgnoreOpenSides ? EnumSet.noneOf(Side.class)
				: SubstanceCoreUtilities.getSides(button,
						SubstanceLookAndFeel.BUTTON_OPEN_SIDE_PROPERTY);
		String openKey = "";
		for (Side oSide : openSides) {
			openKey += oSide.name() + "-";
		}
		boolean noBorder = SubstanceCoreUtilities.isSpinnerButton(button);
		// && !button.getParent().isEnabled();
		String sideKey = (side == null) ? "null" : side.toString();
		HashMapKey key = SubstanceCoreUtilities.getHashKey(width, height,
				sideKey, cyclePos, openKey, colorScheme.getDisplayName(),
				colorScheme2.getDisplayName(), borderScheme.getDisplayName(),
				borderScheme2.getDisplayName(), button.getClass().getName(),
				painter.getDisplayName(), noBorder);
		// System.out.println("\tKey " + key);
		if (!pairwiseBackgrounds.containsKey(key)) {
			// System.out.println("\tNot found");

			// buttons will be rectangular to make two scrolls (horizontal
			// and vertical) touch the corners.
			int radius = 0;

			int deltaLeft = openSides.contains(Side.LEFT) ? 3 : 0;
			int deltaRight = openSides.contains(Side.RIGHT) ? 3 : 0;
			int deltaTop = openSides.contains(Side.TOP) ? 3 : 0;
			int deltaBottom = openSides.contains(Side.BOTTOM) ? 3 : 0;

			// if ((side == Side.TOP) || (side == Side.BOTTOM)) {
			// int temp = deltaLeft;
			// deltaLeft = deltaBottom;
			// deltaBottom = deltaRight;
			// deltaRight = deltaTop;
			// deltaTop = temp;
			// }

			// System.out.println(button.getName() + ":" + deltaLeft + ":" +
			// deltaRight + ":" + deltaTop + ":" + deltaBottom);

			GeneralPath contour = null;

			// SubstanceGradientPainter painter = new StandardGradientPainter();

			SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
					.getBorderPainter(button);

			int borderDelta = (int) Math.floor(SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(button)) / 2.0);
			BufferedImage finalBackground = SubstanceCoreUtilities
					.getBlankImage(width, height);
			Graphics2D finalGraphics = (Graphics2D) finalBackground
					.getGraphics();
			// finalGraphics.setColor(Color.red);
			// finalGraphics.fillRect(0, 0, width, height);
			finalGraphics.translate(-deltaLeft, -deltaTop);
			if (side != null) {
				switch (side) {
				case TOP:
				case BOTTOM:
					// rotate by 90% for better visuals
					contour = SubstanceOutlineUtilities.getBaseOutline(height
							+ deltaTop + deltaBottom, width + deltaLeft
							+ deltaRight, radius, null, borderDelta);

					int translateY = height;
					if (SubstanceCoreUtilities.isScrollButton(button)) {
						translateY += (1 + ((side == SubstanceConstants.Side.BOTTOM) ? 1
								: -2));
					}
					AffineTransform at = AffineTransform.getTranslateInstance(
							0, translateY);
					at.rotate(-Math.PI / 2);
					finalGraphics.setTransform(at);

					painter.paintContourBackground(finalGraphics, button,
							height + deltaTop + deltaBottom, width + deltaLeft
									+ deltaRight, contour, false, colorScheme,
							colorScheme2, cyclePos, true,
							colorScheme != colorScheme2);
					if (!noBorder) {
						borderPainter.paintBorder(finalGraphics, button, height
								+ deltaTop + deltaBottom, width + deltaLeft
								+ deltaRight, contour, null, borderScheme,
								borderScheme2, cyclePos,
								borderScheme != borderScheme2);
					}
					break;
				case RIGHT:
				case LEFT:
					// arrow in horizontal bar
					contour = SubstanceOutlineUtilities.getBaseOutline(width
							+ deltaLeft + deltaRight, height + deltaTop
							+ deltaBottom, radius, null, borderDelta);

					painter.paintContourBackground(finalGraphics, button, width
							+ deltaLeft + deltaRight, height + deltaTop
							+ deltaBottom, contour, false, colorScheme,
							colorScheme2, cyclePos, true,
							colorScheme != colorScheme2);
					if (!noBorder) {
						borderPainter.paintBorder(finalGraphics, button, width
								+ deltaLeft + deltaRight, height + deltaTop
								+ deltaBottom, contour, null, borderScheme,
								borderScheme2, cyclePos,
								borderScheme != borderScheme2);
					}
					break;
				}
			} else {
				contour = SubstanceOutlineUtilities.getBaseOutline(width
						+ deltaLeft + deltaRight, height + deltaTop
						+ deltaBottom, radius, null, borderDelta);

				painter.paintContourBackground(finalGraphics, button, width
						+ deltaLeft + deltaRight, height + deltaTop
						+ deltaBottom, contour, false, colorScheme,
						colorScheme2, cyclePos, true,
						colorScheme != colorScheme2);
				if (!noBorder) {
					borderPainter.paintBorder(finalGraphics, button, width
							+ deltaLeft + deltaRight, height + deltaTop
							+ deltaBottom, contour, null, borderScheme,
							borderScheme2, cyclePos,
							borderScheme != borderScheme2);
				}
			}

			// System.out.println("\tCreated new background " + width + ":" +
			// height);
			pairwiseBackgrounds.put(key, finalBackground);
		}
		BufferedImage opaque = pairwiseBackgrounds.get(key);
		return opaque;
	}
}
