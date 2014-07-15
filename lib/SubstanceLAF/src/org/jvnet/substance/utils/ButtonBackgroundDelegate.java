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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.*;
import javax.swing.JInternalFrame.JDesktopIcon;

import org.jvnet.lafwidget.animation.*;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.*;
import org.jvnet.substance.api.SubstanceConstants.Side;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;
import org.jvnet.substance.shaper.*;

/**
 * Delegate class for painting backgrounds of buttons in <b>Substance </b> look
 * and feel. This class is <b>for internal use only</b>.
 * 
 * @author Kirill Grouchnikov
 */
public class ButtonBackgroundDelegate {
	/**
	 * Cache for background images. Each time
	 * {@link #getBackground(AbstractButton, SubstanceButtonShaper, SubstanceGradientPainter, int, int)}
	 * is called, it checks <code>this</code> map to see if it already contains
	 * such background. If so, the background from the map is returned.
	 */
	private static LazyResettableHashMap<BufferedImage> regularBackgrounds = new LazyResettableHashMap<BufferedImage>(
			"ButtonBackgroundDelegate");

	/**
	 * Retrieves the background for the specified button.
	 * 
	 * @param button
	 *            Button.
	 * @param model
	 *            Button model.
	 * @param shaper
	 *            Button shaper.
	 * @param painter
	 *            Button gradient painter.
	 * @param borderPainter
	 *            Button border painter.
	 * @param width
	 *            Button width.
	 * @param height
	 *            Button height.
	 * @return Button background.
	 */
	public static BufferedImage getFullAlphaBackground(AbstractButton button,
			ButtonModel model, SubstanceButtonShaper shaper,
			SubstanceGradientPainter painter,
			SubstanceBorderPainter borderPainter, int width, int height) {
		ComponentState state = ComponentState.getState(model, button);
		ComponentState prevState = SubstanceCoreUtilities
				.getPrevComponentState(button, null);

		// compute cycle count (for animation)
		float cyclePos = state.getCyclePosition();
		boolean isPulsating = false;
		if (button instanceof JButton) {
			JButton jb = (JButton) button;
			if (PulseTracker.isPulsating(jb)
					&& (state != ComponentState.PRESSED_SELECTED)
					&& (state != ComponentState.PRESSED_UNSELECTED)) {
				isPulsating = true;
				cyclePos = (int) (PulseTracker.getCycles(jb) % 20);
				if (cyclePos > 10) {
					cyclePos = 19 - cyclePos;
				}
				cyclePos /= 10.0f;
			}
		}

		// compute color scheme
		SubstanceColorScheme colorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, state);
		SubstanceColorScheme colorScheme2 = colorScheme;
		SubstanceColorScheme borderScheme = SubstanceColorSchemeUtilities
				.getColorScheme(button, ColorSchemeAssociationKind.BORDER,
						state);
		SubstanceColorScheme borderScheme2 = borderScheme;

		// see if need to use attention-drawing animation
		boolean isWindowModified = false;
		if (SubstanceCoreUtilities.isTitleCloseButton(button)) {
			// check if have windowModified property
			Component comp = button;
			while (comp != null) {
				if (comp instanceof JInternalFrame) {
					JInternalFrame jif = (JInternalFrame) comp;
					isWindowModified = Boolean.TRUE
							.equals(jif
									.getClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED));
					break;
				}
				if (comp instanceof JRootPane) {
					JRootPane jrp = (JRootPane) comp;
					isWindowModified = Boolean.TRUE
							.equals(jrp
									.getClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED));
					break;
				}
				if (comp instanceof JDesktopIcon) {
					JDesktopIcon jdi = (JDesktopIcon) comp;
					JInternalFrame jif = jdi.getInternalFrame();
					isWindowModified = Boolean.TRUE
							.equals(jif
									.getClientProperty(SubstanceLookAndFeel.WINDOW_MODIFIED));
					break;
				}
				comp = comp.getParent();
			}
			if (isWindowModified) {
				colorScheme2 = SubstanceColorSchemeUtilities.YELLOW;
				colorScheme = SubstanceColorSchemeUtilities.ORANGE;
			}
		}

		// see if need to use fade animation. Important - don't do it
		// on pulsating buttons (such as default or close buttons
		// of modified frames).
		if (!isWindowModified && !isPulsating && model.isEnabled()) {
			FadeState fadeState = SubstanceFadeUtilities.getFadeState(button,
					FadeKind.ROLLOVER, FadeKind.SELECTION, FadeKind.PRESS);
			if (fadeState != null) {
				colorScheme = SubstanceColorSchemeUtilities.getColorScheme(
						button, state);
				colorScheme2 = SubstanceColorSchemeUtilities.getColorScheme(
						button, prevState);
				borderScheme = SubstanceColorSchemeUtilities.getColorScheme(
						button, ColorSchemeAssociationKind.BORDER, state);
				borderScheme2 = SubstanceColorSchemeUtilities.getColorScheme(
						button, ColorSchemeAssociationKind.BORDER, prevState);
				cyclePos = fadeState.getFadePosition();

				// System.out.println(extraModelKey + ":" + cyclePos);
				if (fadeState.isFadingIn())
					cyclePos = 1.0f - cyclePos;
			} else {
				// System.out.println(extraModelKey + ":" + state.name());
			}
		}
		// if (colorScheme == colorScheme2) {
		// System.out.println(extraModelKey + ":" + state.name() + ":"
		// + colorScheme.getDisplayName());
		// }

		// compute the straight sides
		Set<SubstanceConstants.Side> straightSides = SubstanceCoreUtilities
				.getSides(button, SubstanceLookAndFeel.BUTTON_SIDE_PROPERTY);
		String straightKey = "";
		for (Side sSide : straightSides) {
			straightKey += sSide.name() + "-";
		}

		boolean isRoundButton = StandardButtonShaper.isRoundButton(button);
		float radius = 0.0f;
		if (shaper instanceof RectangularButtonShaper) {
			radius = ((RectangularButtonShaper) shaper).getCornerRadius(button,
					null);
		}

		Set<Side> openSides = SubstanceCoreUtilities.getSides(button,
				SubstanceLookAndFeel.BUTTON_OPEN_SIDE_PROPERTY);
		// String openKey = "";
		// for (Side oSide : openSides) {
		// openKey += oSide.name() + "-";
		// }
		// String extraModelKey = "";
		// for (String modelKey : extraModelKeys) {
		// extraModelKey += (modelKey + "-");
		// }
		boolean isContentAreaFilled = button.isContentAreaFilled();
		boolean isBorderPainted = button.isBorderPainted();
		HashMapKey key = SubstanceCoreUtilities.getHashKey(width, height,
				prevState.name(), state.name(), cyclePos, colorScheme
						.getDisplayName(), colorScheme2.getDisplayName(),
				borderScheme.getDisplayName(), borderScheme2.getDisplayName(),
				shaper.getDisplayName(), painter.getDisplayName(),
				borderPainter.getDisplayName(), straightKey, openSides, button
						.getClass().getName(), isRoundButton, radius,
				isContentAreaFilled, isBorderPainted, SubstanceSizeUtils
						.getComponentFontSize(button));

		// System.out.println(component.getClass().getSimpleName() + ":"
		// + extraModelKeys + " "
		// + /* button.getText() + ":" + */prevState.name() + "->"
		// + state.name() + " - " + cyclePos);
		// System.out.println("\t" + colorScheme2.getDisplayName() + ":"
		// + colorScheme.getDisplayName());

		BufferedImage existing = regularBackgrounds.get(key);
		if (existing == null) {
			int openDelta = (int) (Math.ceil(3.0 * SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(button))));
			int deltaLeft = openSides.contains(Side.LEFT) ? openDelta : 0;
			int deltaRight = openSides.contains(Side.RIGHT) ? openDelta : 0;
			int deltaTop = openSides.contains(Side.TOP) ? openDelta : 0;
			int deltaBottom = openSides.contains(Side.BOTTOM) ? openDelta : 0;

			// System.err.println(key);
			int borderDelta = (int) Math.floor(SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(button)) / 2.0);
			Shape contour = shaper.getButtonOutline(button, new Insets(
					borderDelta, borderDelta, borderDelta, borderDelta), width
					+ deltaLeft + deltaRight, height + deltaTop + deltaBottom,
					false);

			BufferedImage newBackground = SubstanceCoreUtilities.getBlankImage(
					width, height);
			Graphics2D finalGraphics = (Graphics2D) newBackground.getGraphics();
			finalGraphics.translate(-deltaLeft, -deltaTop);
			if (isContentAreaFilled) {
				painter.paintContourBackground(finalGraphics, button, width
						+ deltaLeft + deltaRight, height + deltaTop
						+ deltaBottom, contour, false, colorScheme,
						colorScheme2, cyclePos, true,
						colorScheme != colorScheme2);
			}

			if (isBorderPainted) {
				int borderThickness = (int) SubstanceSizeUtils
						.getBorderStrokeWidth(SubstanceSizeUtils
								.getComponentFontSize(button));
				Shape contourInner = borderPainter.isPaintingInnerContour() ? shaper
						.getButtonOutline(button, new Insets(borderDelta
								+ borderThickness, borderDelta
								+ borderThickness, borderDelta
								+ borderThickness, borderDelta
								+ borderThickness), width + deltaLeft
								+ deltaRight, height + deltaTop + deltaBottom,
								true)
						: null;
				borderPainter.paintBorder(finalGraphics, button, width
						+ deltaLeft + deltaRight, height + deltaTop
						+ deltaBottom, contour, contourInner, borderScheme,
						borderScheme2, cyclePos, borderScheme != borderScheme2);
			}

			regularBackgrounds.put(key, newBackground);
		}
		existing = regularBackgrounds.get(key);
		// existing.validate(GraphicsEnvironment.getLocalGraphicsEnvironment()
		// .getDefaultScreenDevice().getDefaultConfiguration());
		return existing;
	}

	/**
	 * Simple constructor.
	 */
	public ButtonBackgroundDelegate() {
		super();
	}

	/**
	 * Updates background of the specified button.
	 * 
	 * @param g
	 *            Graphic context.
	 * @param button
	 *            Button to update.
	 */
	public void updateBackground(Graphics g, AbstractButton button) {
		// failsafe for LAF change
		if (!SubstanceLookAndFeel.isCurrentLookAndFeel())
			return;

		if (SubstanceCoreUtilities.isButtonNeverPainted(button))
			return;

		int width = button.getWidth();
		int height = button.getHeight();
		int y = 0;
		if (SubstanceCoreUtilities.isScrollButton(button)
				|| SubstanceCoreUtilities.isSpinnerButton(button)) {
			Sideable sideable = (Sideable) button;
			PairwiseButtonBackgroundDelegate.updatePairwiseBackground(g,
					button, width, height, sideable.getSide(), false);
			return;
		}

		SubstanceGradientPainter painter = SubstanceCoreUtilities
				.getGradientPainter(button);
		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(button);
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(button);

		BufferedImage bgImage = getFullAlphaBackground(button, button
				.getModel(), shaper, painter, borderPainter, width, height);
		ComponentState state = ComponentState.getState(button);

		// Two special cases here:
		// 1. Button has flat appearance.
		// 2. Button is disabled.
		// For both cases, we need to set custom translucency.
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
			graphics.drawImage(bgImage, 0, y, null);
			graphics.dispose();
		}
	}

	/**
	 * Checks whether the specified button has round corners.
	 * 
	 * @param button
	 *            Button to check.
	 * @return <code>true</code> if the specified button has round corners,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isRoundButton(AbstractButton button) {
		return (!SubstanceCoreUtilities.isComboBoxButton(button))
				&& (!SubstanceCoreUtilities.isScrollButton(button))
				&& SubstanceCoreUtilities.hasText(button);
	}

	/**
	 * Returns <code>true</code> if the specified <i>x,y </i> location is
	 * contained within the look and feel's defined shape of the specified
	 * component. <code>x</code> and <code>y</code> are defined to be relative
	 * to the coordinate system of the specified component.
	 * 
	 * @param button
	 *            the component where the <i>x,y </i> location is being queried;
	 * @param x
	 *            the <i>x </i> coordinate of the point
	 * @param y
	 *            the <i>y </i> coordinate of the point
	 * @return <code>true</code> if the specified <i>x,y </i> location is
	 *         contained within the look and feel's defined shape of the
	 *         specified component, <code>false</code> otherwise.
	 */
	public static boolean contains(AbstractButton button, int x, int y) {
		// failsafe for LAF change
		if (!SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			return false;
		}
		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(button);
		if (shaper == null)
			return false;
		Shape contour = shaper.getButtonOutline(button, null,
				button.getWidth(), button.getHeight(), false);
		return contour.contains(x, y);
	}

	/**
	 * Returns the memory usage string.
	 * 
	 * @return Memory usage string.
	 */
	static String getMemoryUsage() {
		StringBuffer sb = new StringBuffer();
		sb.append("SubstanceBackgroundDelegate: \n");
		sb.append("\t" + regularBackgrounds.size() + " regular");
		// + pairwiseBackgrounds.size() + " pairwise");
		return sb.toString();
	}

}
