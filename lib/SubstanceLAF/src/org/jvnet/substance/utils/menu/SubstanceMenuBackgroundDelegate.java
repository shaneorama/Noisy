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
package org.jvnet.substance.utils.menu;

import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;

import javax.swing.*;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.lafwidget.animation.FadeState;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.api.ComponentState;
import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.SubstanceConstants.MenuGutterFillKind;
import org.jvnet.substance.painter.utils.BackgroundPaintingUtils;
import org.jvnet.substance.painter.utils.HighlightPainterUtils;
import org.jvnet.substance.utils.*;

/**
 * Delegate for painting background of menu items.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceMenuBackgroundDelegate {
	/**
	 * Updates the specified menu item with the background that matches the
	 * provided parameters.
	 * 
	 * @param g
	 *            Graphic context.
	 * @param menuItem
	 *            Menu item.
	 * @param bgColor
	 *            Current background color.
	 * @param borderAlpha
	 *            Border alpha.
	 * @param textOffset
	 *            The offset of the menu item text.
	 */
	public static void paintBackground(Graphics g, Component menuItem,
			int textOffset) {
		if (!menuItem.isShowing())
			return;
		int menuWidth = menuItem.getWidth();
		int menuHeight = menuItem.getHeight();

		Graphics2D graphics = (Graphics2D) g.create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		BackgroundPaintingUtils.update(graphics, menuItem, false);

		if (menuItem.getParent() instanceof JPopupMenu) {
			if (menuItem.getComponentOrientation().isLeftToRight()) {
				MenuGutterFillKind fillKind = SubstanceCoreUtilities
						.getMenuGutterFillKind();
				if (fillKind != MenuGutterFillKind.NONE) {
					SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
							.getColorScheme(menuItem, ComponentState.DEFAULT);
					Color leftColor = ((fillKind == MenuGutterFillKind.SOFT_FILL) || (fillKind == MenuGutterFillKind.HARD)) ? scheme
							.getUltraLightColor()
							: scheme.getLightColor();
					Color rightColor = ((fillKind == MenuGutterFillKind.SOFT_FILL) || (fillKind == MenuGutterFillKind.SOFT)) ? scheme
							.getUltraLightColor()
							: scheme.getLightColor();
					LinearGradientPaint gp = new LinearGradientPaint(0, 0,
							textOffset, 0, new float[] { 0.0f, 1.0f },
							new Color[] { leftColor, rightColor },
							CycleMethod.REPEAT);
					graphics.setComposite(TransitionLayout.getAlphaComposite(
							menuItem, 0.7f, g));

					// System.out.println(menuItem.getText()
					// + "["
					// + menuItem.isEnabled()
					// + "] : "
					// + ((AlphaComposite) graphics.getComposite())
					// .getAlpha() + ", " + leftColor + "->"
					// + rightColor);
					//
					graphics.setPaint(gp);
					graphics.fillRect(0, 0, textOffset - 2, menuHeight);
				}
			} else {
				// fix for defect 125 - support of RTL menus
				MenuGutterFillKind fillKind = SubstanceCoreUtilities
						.getMenuGutterFillKind();
				if (fillKind != MenuGutterFillKind.NONE) {
					SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
							.getColorScheme(menuItem, ComponentState.DEFAULT);
					Color leftColor = ((fillKind == MenuGutterFillKind.HARD_FILL) || (fillKind == MenuGutterFillKind.HARD)) ? scheme
							.getLightColor()
							: scheme.getUltraLightColor();
					Color rightColor = ((fillKind == MenuGutterFillKind.HARD_FILL) || (fillKind == MenuGutterFillKind.SOFT)) ? scheme
							.getLightColor()
							: scheme.getUltraLightColor();

					LinearGradientPaint gp = new LinearGradientPaint(
							textOffset, 0, menuWidth, 0, new float[] { 0.0f,
									1.0f },
							new Color[] { leftColor, rightColor },
							CycleMethod.REPEAT);
					graphics.setComposite(TransitionLayout.getAlphaComposite(
							menuItem, 0.7f, g));
					graphics.setPaint(gp);
					graphics.fillRect(textOffset - 2, 0, menuWidth, menuHeight);
				}
			}
		}
		// }

		graphics.dispose();
	}

	/**
	 * Paints menu highlights.
	 * 
	 * @param g
	 *            Graphics context.
	 * @param menuItem
	 *            Menu item.
	 * @param borderAlpha
	 *            Alpha channel for painting the border.
	 */
	public static void paintHighlights(Graphics g, JMenuItem menuItem,
			float borderAlpha) {
		Graphics2D graphics = (Graphics2D) g.create();

		ButtonModel model = menuItem.getModel();
		// int menuWidth = menuItem.getWidth();
		// int menuHeight = menuItem.getHeight();

		ComponentState prevState = SubstanceCoreUtilities
				.getPrevComponentState(menuItem);
		ComponentState currState = ComponentState.getState(model, menuItem,
				!(menuItem instanceof JMenu));

		// Compute the alpha values for the animation (the highlights
		// have separate alpha channels, so that rollover animation can start
		// from 0.0 alpha and goes to 0.4 alpha on non-selected cells,
		// but on selected cells can go from 0.7 to 0.9). We need to respect
		// these border values for proper visual transitions
		float startAlpha = SubstanceColorSchemeUtilities.getHighlightAlpha(
				menuItem, prevState);
		float endAlpha = SubstanceColorSchemeUtilities.getHighlightAlpha(
				menuItem, currState);

		FadeState state = SubstanceFadeUtilities.getFadeState(menuItem,
				FadeKind.SELECTION, FadeKind.ARM, FadeKind.ROLLOVER);
		float fadeCoef = 0.0f;
		float totalAlpha = endAlpha;
		if (state != null) {
			fadeCoef = state.getFadePosition();

			// compute the total alpha of the overlays.
			if (state.isFadingIn()) {
				totalAlpha = startAlpha + (endAlpha - startAlpha) * fadeCoef;
			} else {
				totalAlpha = startAlpha + (endAlpha - startAlpha)
						* (1.0f - fadeCoef);
			}

			if (state.isFadingIn())
				fadeCoef = 1.0f - fadeCoef;

			// System.out.println(menuItem.getText() + " from " +
			// prevState.name()
			// + "[" + alphaForPrevBackground + "] to " + currState.name()
			// + "[" + alphaForCurrBackground + "] at " + fadeCoef);
			// System.out.println("From " + prevScheme.getDisplayName() +
			// " to :"
			// + currScheme.getDisplayName());

		}

		// System.out.println(menuItem.getText() + "[" + currState.name() + "]:"
		// + prevScheme.getDisplayName() + "[" + alphaForPrevBackground
		// + "]:" + currScheme.getDisplayName() + "["
		// + alphaForCurrBackground + "]");
		// System.out.println("ARM:" + menuItem.getModel().isArmed() + ", ENA:"
		// + menuItem.getModel().isEnabled() + ", PRE:"
		// + menuItem.getModel().isPressed() + ", ROL:"
		// + menuItem.getModel().isRollover() + ", SEL:"
		// + menuItem.getModel().isSelected());

		// fix for defect 103 - no rollover effects on menu items
		// that are not in the selected menu path
		MenuElement[] selectedMenuPath = MenuSelectionManager.defaultManager()
				.getSelectedPath();
		boolean isRollover = (selectedMenuPath.length == 0);
		for (MenuElement elem : selectedMenuPath) {
			if (elem == menuItem) {
				isRollover = true;
				break;
			}
		}
		isRollover = isRollover && model.isRollover();

		boolean hasHighlight = (state != null) || model.isArmed() || isRollover
				|| ((menuItem instanceof JMenu) && model.isSelected());

		if (hasHighlight && (totalAlpha > 0.0f)) {
			graphics.setComposite(TransitionLayout.getAlphaComposite(menuItem,
					totalAlpha, g));
			HighlightPainterUtils.paintHighlight(graphics, null, menuItem,
					new Rectangle(0, 0, menuItem.getWidth(), menuItem
							.getHeight()), borderAlpha, null, currState,
					prevState, fadeCoef);
			graphics.setComposite(TransitionLayout.getAlphaComposite(menuItem,
					g));
		}
		graphics.dispose();
	}
}
