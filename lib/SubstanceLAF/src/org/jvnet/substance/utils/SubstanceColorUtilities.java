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

import java.awt.Color;
import java.awt.Component;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.lafwidget.animation.FadeState;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.*;

/**
 * Various color-related utilities. This class is <b>for internal use only</b>.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceColorUtilities {
	/**
	 * Returns the color of the top portion of border in control backgrounds.
	 * 
	 * @param scheme1
	 *            The first color scheme.
	 * @param scheme2
	 *            The second color scheme.
	 * @param cycleCoef
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0.0 .. 1.0 range.
	 * @return The color of the top portion of border in control backgrounds.
	 */
	public static Color getTopBorderColor(SubstanceColorScheme scheme1,
			SubstanceColorScheme scheme2, double cycleCoef) {
		return SubstanceColorUtilities.getInterpolatedColor(scheme1
				.getUltraDarkColor(), scheme2.getUltraDarkColor(), cycleCoef);
	}

	/**
	 * Returns the color of the middle portion of border in control backgrounds.
	 * 
	 * @param scheme1
	 *            The first color scheme.
	 * @param scheme2
	 *            The second color scheme.
	 * @param cycleCoef
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0.0 .. 1.0 range.
	 * @return The color of the middle portion of border in control backgrounds.
	 */
	public static Color getMidBorderColor(SubstanceColorScheme scheme1,
			SubstanceColorScheme scheme2, double cycleCoef) {
		return SubstanceColorUtilities.getInterpolatedColor(scheme1
				.getDarkColor(), scheme2.getDarkColor(), cycleCoef);
	}

	/**
	 * Returns the color of the bottom portion of border in control backgrounds.
	 * 
	 * @param scheme1
	 *            The first color scheme.
	 * @param scheme2
	 *            The second color scheme.
	 * @param cycleCoef
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0.0 .. 1.0 range.
	 * @return The color of the bottom portion of border in control backgrounds.
	 */
	public static Color getBottomBorderColor(SubstanceColorScheme scheme1,
			SubstanceColorScheme scheme2, double cycleCoef) {
		Color c1 = SubstanceColorUtilities.getInterpolatedColor(scheme1
				.getDarkColor(), scheme1.getMidColor(), 0.5);
		Color c2 = SubstanceColorUtilities.getInterpolatedColor(scheme2
				.getDarkColor(), scheme2.getMidColor(), 0.5);
		return SubstanceColorUtilities.getInterpolatedColor(c1, c2, cycleCoef);
	}

	/**
	 * Returns the color of the top portion of fill in control backgrounds.
	 * 
	 * @param scheme1
	 *            The first color scheme.
	 * @param scheme2
	 *            The second color scheme.
	 * @param cycleCoef
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0.0 .. 1.0 range.
	 * @param useCyclePosAsInterpolation
	 *            Indicates the algorithm to use for computing various colors.
	 *            If <code>true</code>, the <code>cyclePos</code> is used to
	 *            interpolate colors between different color components of both
	 *            color schemes. If <code>false</code>, the
	 *            <code>cyclePos</code> is used to interpolate colors between
	 *            different color components of the first color scheme.
	 * @return The color of the top portion of fill in control backgrounds.
	 */
	public static Color getTopFillColor(SubstanceColorScheme scheme1,
			SubstanceColorScheme scheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		if (!useCyclePosAsInterpolation) {
			Color c = SubstanceColorUtilities.getInterpolatedColor(scheme1
					.getDarkColor(), scheme1.getMidColor(), 0.4);
			return SubstanceColorUtilities.getInterpolatedColor(c, scheme2
					.getLightColor(), cycleCoef);
		} else {
			Color c1 = SubstanceColorUtilities.getInterpolatedColor(scheme1
					.getDarkColor(), scheme1.getMidColor(), 0.4);
			Color c2 = SubstanceColorUtilities.getInterpolatedColor(scheme2
					.getDarkColor(), scheme2.getMidColor(), 0.4);
			return SubstanceColorUtilities.getInterpolatedColor(c1, c2,
					cycleCoef);
		}
	}

	/**
	 * Returns the color of the middle portion of fill in control backgrounds.
	 * 
	 * @param scheme1
	 *            The first color scheme.
	 * @param scheme2
	 *            The second color scheme.
	 * @param cycleCoef
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0.0 .. 1.0 range.
	 * @param useCyclePosAsInterpolation
	 *            Indicates the algorithm to use for computing various colors.
	 *            If <code>true</code>, the <code>cyclePos</code> is used to
	 *            interpolate colors between different color components of both
	 *            color schemes. If <code>false</code>, the
	 *            <code>cyclePos</code> is used to interpolate colors between
	 *            different color components of the first color scheme.
	 * @return The color of the middle portion of fill in control backgrounds.
	 */
	public static Color getMidFillColor(SubstanceColorScheme scheme1,
			SubstanceColorScheme scheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		if (!useCyclePosAsInterpolation) {
			return SubstanceColorUtilities.getInterpolatedColor(scheme1
					.getMidColor(), scheme2.getLightColor(), cycleCoef);
		} else {
			return SubstanceColorUtilities.getInterpolatedColor(scheme1
					.getMidColor(), scheme2.getMidColor(), cycleCoef);
		}
	}

	/**
	 * Returns the color of the bottom portion of fill in control backgrounds.
	 * 
	 * @param scheme1
	 *            The first color scheme.
	 * @param scheme2
	 *            The second color scheme.
	 * @param cycleCoef
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0.0 .. 1.0 range.
	 * @param useCyclePosAsInterpolation
	 *            Indicates the algorithm to use for computing various colors.
	 *            If <code>true</code>, the <code>cyclePos</code> is used to
	 *            interpolate colors between different color components of both
	 *            color schemes. If <code>false</code>, the
	 *            <code>cyclePos</code> is used to interpolate colors between
	 *            different color components of the first color scheme.
	 * @return The color of the bottom portion of fill in control backgrounds.
	 */
	public static Color getBottomFillColor(SubstanceColorScheme scheme1,
			SubstanceColorScheme scheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		if (!useCyclePosAsInterpolation) {
			return SubstanceColorUtilities.getInterpolatedColor(scheme1
					.getUltraLightColor(), scheme2.getExtraLightColor(),
					cycleCoef);
		} else {
			return SubstanceColorUtilities.getInterpolatedColor(scheme1
					.getUltraLightColor(), scheme2.getUltraLightColor(),
					cycleCoef);
		}
	}

	/**
	 * Returns the color of the top portion of shine in control backgrounds.
	 * 
	 * @param scheme1
	 *            The first color scheme.
	 * @param scheme2
	 *            The second color scheme.
	 * @param cycleCoef
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0.0 .. 1.0 range.
	 * @param useCyclePosAsInterpolation
	 *            Indicates the algorithm to use for computing various colors.
	 *            If <code>true</code>, the <code>cyclePos</code> is used to
	 *            interpolate colors between different color components of both
	 *            color schemes. If <code>false</code>, the
	 *            <code>cyclePos</code> is used to interpolate colors between
	 *            different color components of the first color scheme.
	 * @return The color of the top portion of shine in control backgrounds.
	 */
	public static Color getTopShineColor(SubstanceColorScheme scheme1,
			SubstanceColorScheme scheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return getBottomFillColor(scheme1, scheme2, cycleCoef,
				useCyclePosAsInterpolation);
	}

	/**
	 * Returns the color of the bottom portion of shine in control backgrounds.
	 * 
	 * @param scheme1
	 *            The first color scheme.
	 * @param scheme2
	 *            The second color scheme.
	 * @param cycleCoef
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0.0 .. 1.0 range.
	 * @param useCyclePosAsInterpolation
	 *            Indicates the algorithm to use for computing various colors.
	 *            If <code>true</code>, the <code>cyclePos</code> is used to
	 *            interpolate colors between different color components of both
	 *            color schemes. If <code>false</code>, the
	 *            <code>cyclePos</code> is used to interpolate colors between
	 *            different color components of the first color scheme.
	 * @return The color of the bottom portion of shine in control backgrounds.
	 */
	public static Color getBottomShineColor(SubstanceColorScheme scheme1,
			SubstanceColorScheme scheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		if (!useCyclePosAsInterpolation) {
			return SubstanceColorUtilities.getInterpolatedColor(scheme1
					.getLightColor(), scheme2.getUltraLightColor(), cycleCoef);
		} else {
			return SubstanceColorUtilities.getInterpolatedColor(scheme1
					.getLightColor(), scheme2.getLightColor(), cycleCoef);
		}
	}

	/**
	 * Interpolates color.
	 * 
	 * @param color1
	 *            The first color
	 * @param color2
	 *            The second color
	 * @param color1Likeness
	 *            The closer this value is to 0.0, the closer the resulting
	 *            color will be to <code>color2</code>.
	 * @return Interpolated RGB value.
	 */
	public static int getInterpolatedRGB(Color color1, Color color2,
			double color1Likeness) {
		if ((color1Likeness < 0.0) || (color1Likeness > 1.0))
			throw new IllegalArgumentException(
					"Color likeness should be in 0.0-1.0 range [is "
							+ color1Likeness + "]");
		int lr = color1.getRed();
		int lg = color1.getGreen();
		int lb = color1.getBlue();
		int la = color1.getAlpha();
		int dr = color2.getRed();
		int dg = color2.getGreen();
		int db = color2.getBlue();
		int da = color2.getAlpha();

		// using some interpolation values (such as 0.29 from issue 401)
		// results in an incorrect final value without Math.round.
		int r = (lr == dr) ? lr : (int) Math.round(color1Likeness * lr
				+ (1.0 - color1Likeness) * dr);
		int g = (lg == dg) ? lg : (int) Math.round(color1Likeness * lg
				+ (1.0 - color1Likeness) * dg);
		int b = (lb == db) ? lb : (int) Math.round(color1Likeness * lb
				+ (1.0 - color1Likeness) * db);
		int a = (la == da) ? la : (int) Math.round(color1Likeness * la
				+ (1.0 - color1Likeness) * da);

		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	/**
	 * Interpolates color.
	 * 
	 * @param color1
	 *            The first color
	 * @param color2
	 *            The second color
	 * @param color1Likeness
	 *            The closer this value is to 0.0, the closer the resulting
	 *            color will be to <code>color2</code>.
	 * @return Interpolated color.
	 */
	public static Color getInterpolatedColor(Color color1, Color color2,
			double color1Likeness) {
		if (color1.equals(color2))
			return color1;
		if (color1Likeness == 1.0)
			return color1;
		if (color1Likeness == 0.0)
			return color2;
		return new Color(getInterpolatedRGB(color1, color2, color1Likeness),
				true);
	}

	/**
	 * Inverts the specified color.
	 * 
	 * @param color
	 *            The original color.
	 * @return The inverted color.
	 */
	public static Color invertColor(Color color) {
		return new Color(255 - color.getRed(), 255 - color.getGreen(),
				255 - color.getBlue(), color.getAlpha());
	}

	/**
	 * Returns a negative of the specified color.
	 * 
	 * @param color
	 *            Color.
	 * @return Negative of the specified color.
	 */
	public static Color getNegativeColor(Color color) {
		return new Color(255 - color.getRed(), 255 - color.getGreen(),
				255 - color.getBlue(), color.getAlpha());
	}

	/**
	 * Returns a negative of the specified color.
	 * 
	 * @param rgb
	 *            Color RGB.
	 * @return Negative of the specified color.
	 */
	public static int getNegativeColor(int rgb) {
		int transp = (rgb >>> 24) & 0xFF;
		int r = (rgb >>> 16) & 0xFF;
		int g = (rgb >>> 8) & 0xFF;
		int b = (rgb >>> 0) & 0xFF;

		return (transp << 24) | ((255 - r) << 16) | ((255 - g) << 8)
				| (255 - b);
	}

	/**
	 * Returns a translucent of the specified color.
	 * 
	 * @param color
	 *            Color.
	 * @param alpha
	 *            Alpha channel value.
	 * @return Translucent of the specified color that matches the requested
	 *         alpha channel value.
	 */
	public static Color getAlphaColor(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(),
				alpha);
	}

	/**
	 * Returns saturated version of the specified color.
	 * 
	 * @param color
	 *            Color.
	 * @param factor
	 *            Saturation factor.
	 * @return Saturated color.
	 */
	public static Color getSaturatedColor(Color color, double factor) {
		float[] hsbvals = new float[3];
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(),
				hsbvals);
		float sat = hsbvals[1];
		if (factor > 0.0) {
			sat = sat + (float) factor * (1.0f - sat);
		} else {
			sat = sat + (float) factor * sat;
		}
		return new Color(Color.HSBtoRGB(hsbvals[0], sat, hsbvals[2]));
	}

	/**
	 * Returns hue-shifted (in HSV space) version of the specified color.
	 * 
	 * @param color
	 *            Color.
	 * @param hueShift
	 *            hue shift factor.
	 * @return Hue-shifted (in HSV space) color.
	 */
	public static Color getHueShiftedColor(Color color, double hueShift) {
		float[] hsbvals = new float[3];
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(),
				hsbvals);
		float hue = hsbvals[0];
		hue += hueShift;
		if (hue < 0.0)
			hue += 1.0;
		if (hue > 1.0)
			hue -= 1.0;
		return new Color(Color.HSBtoRGB(hue, hsbvals[1], hsbvals[2]));
	}

	/**
	 * Derives a color based on the original color and a brightness source. The
	 * resulting color has the same hue and saturation as the original color,
	 * but its brightness is shifted towards the brightness of the brightness
	 * source. Thus, a light red color shifted towards dark green will become
	 * dark red.
	 * 
	 * @param original
	 *            Original color.
	 * @param brightnessSource
	 *            Brightness source.
	 * @return Derived color that has the same hue and saturation as the
	 *         original color, but its brightness is shifted towards the
	 *         brightness of the brightness source.
	 */
	public static Color deriveByBrightness(Color original,
			Color brightnessSource) {
		float[] hsbvalsOrig = new float[3];
		Color.RGBtoHSB(original.getRed(), original.getGreen(), original
				.getBlue(), hsbvalsOrig);
		float[] hsbvalsBrightnessSrc = new float[3];
		Color.RGBtoHSB(brightnessSource.getRed(), brightnessSource.getGreen(),
				brightnessSource.getBlue(), hsbvalsBrightnessSrc);
		return new Color(Color.HSBtoRGB(hsbvalsOrig[0], hsbvalsOrig[1],
				(hsbvalsBrightnessSrc[2] + hsbvalsOrig[2]) / 2.0f));

	}

	/**
	 * Returns the foreground color of the specified color scheme.
	 * 
	 * @param scheme
	 *            Color scheme.
	 * @return Color scheme foreground color.
	 */
	public static ColorUIResource getForegroundColor(SubstanceColorScheme scheme) {
		return new ColorUIResource(scheme.getForegroundColor());
	}

	/**
	 * Returns lighter version of the specified color.
	 * 
	 * @param color
	 *            Color.
	 * @param diff
	 *            Difference factor (values closer to 0.0 will produce results
	 *            closer to white color).
	 * @return Lighter version of the specified color.
	 */
	public static Color getLighterColor(Color color, double diff) {
		int r = color.getRed() + (int) (diff * (255 - color.getRed()));
		int g = color.getGreen() + (int) (diff * (255 - color.getGreen()));
		int b = color.getBlue() + (int) (diff * (255 - color.getBlue()));
		return new Color(r, g, b);
	}

	/**
	 * Returns darker version of the specified color.
	 * 
	 * @param color
	 *            Color.
	 * @param diff
	 *            Difference factor (values closer to 1.0 will produce results
	 *            closer to black color).
	 * @return Darker version of the specified color.
	 */
	public static Color getDarkerColor(Color color, double diff) {
		int r = (int) ((1.0 - diff) * color.getRed());
		int g = (int) ((1.0 - diff) * color.getGreen());
		int b = (int) ((1.0 - diff) * color.getBlue());
		return new Color(r, g, b);
	}

	/**
	 * Returns the brightness of the specified color.
	 * 
	 * @param rgb
	 *            RGB value of a color.
	 * @return The brightness of the specified color.
	 */
	public static int getColorBrightness(int rgb) {
		int oldR = (rgb >>> 16) & 0xFF;
		int oldG = (rgb >>> 8) & 0xFF;
		int oldB = (rgb >>> 0) & 0xFF;

		return (222 * oldR + 707 * oldG + 71 * oldB) / 1000;
	}

	/**
	 * Returns the color of the focus ring for the specified component.
	 * 
	 * @param comp
	 *            Component.
	 * @return The color of the focus ring for the specified component.
	 */
	public static Color getFocusColor(Component comp) {
		SubstanceColorScheme activeScheme = SubstanceColorSchemeUtilities
				.getColorScheme(comp, ComponentState.ACTIVE);

		if (comp instanceof AbstractButton) {
			AbstractButton ab = (AbstractButton) comp;
			ButtonModel model = ab.getModel();

			ComponentState currState = ComponentState.getState(model, ab);
			SubstanceColorScheme currScheme = SubstanceColorSchemeUtilities
					.getColorScheme(comp, ColorSchemeAssociationKind.MARK,
							currState);
			Color currColor = currScheme.getFocusRingColor();

			FadeState fadeState = SubstanceFadeUtilities.getFadeState(comp,
					FadeKind.PRESS, FadeKind.SELECTION, FadeKind.ROLLOVER);
			if (fadeState != null) {
				// the component is currently animating
				ComponentState prevState = SubstanceCoreUtilities
						.getPrevComponentState(ab);

				SubstanceColorScheme prevScheme = SubstanceColorSchemeUtilities
						.getColorScheme(comp, ColorSchemeAssociationKind.MARK,
								prevState);

				Color prevColor = prevScheme.getFocusRingColor();

				float likeness = fadeState.getFadePosition();
				if (fadeState.isFadingIn())
					likeness = 1.0f - likeness;
				return SubstanceColorUtilities.getInterpolatedColor(prevColor,
						currColor, likeness);
			} else {
				return currColor;
			}
		}

		Color color = activeScheme.getFocusRingColor();
		return color;
	}

	/**
	 * Returns the color strength.
	 * 
	 * @param color
	 *            Color.
	 * @return Color strength.
	 */
	public static float getColorStrength(Color color) {
		return Math.max(getColorBrightness(color.getRGB()),
				getColorBrightness(getNegativeColor(color.getRGB()))) / 255.0f;
	}

	/**
	 * Returns the color of mark icons (checkbox, radio button, scrollbar
	 * arrows, combo arrows, menu arrows etc) for the specified color scheme.
	 * 
	 * @param colorScheme
	 *            Color scheme.
	 * @param isEnabled
	 *            If <code>true</code>, the mark should be painted in enabled
	 *            state.
	 * @return Color of mark icons.
	 */
	public static Color getMarkColor(SubstanceColorScheme colorScheme,
			boolean isEnabled) {
		if (colorScheme.isDark()) {
			if (!isEnabled) {
				return colorScheme.getDarkColor();

			} else {
				return getInterpolatedColor(colorScheme.getForegroundColor(),
						colorScheme.getUltraLightColor(), 0.9);
			}
		} else {
			Color color1 = isEnabled ? colorScheme.getUltraDarkColor()
					: colorScheme.getUltraDarkColor();
			Color color2 = isEnabled ? colorScheme.getDarkColor() : colorScheme
					.getLightColor();
			return getInterpolatedColor(color1, color2, 0.9);
		}
	}

	/**
	 * Returns the foreground text color of the specified component.
	 * 
	 * @param component
	 *            Component.
	 * @param state
	 *            Component current state.
	 * @param prevState
	 *            Component previous state.
	 * @return The foreground text color of the specified component.
	 */
	public static Color getForegroundColor(Component component,
			ComponentState state, ComponentState prevState) {
		boolean isMenuItem = (component instanceof JMenuItem);
		ColorSchemeAssociationKind currAssocKind = ColorSchemeAssociationKind.FILL;
		// use HIGHLIGHT on active menu items
		if (isMenuItem && state.isKindActive(FadeKind.ENABLE)
				&& (state != ComponentState.DEFAULT))
			currAssocKind = ColorSchemeAssociationKind.HIGHLIGHT;
		SubstanceColorScheme colorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(component, currAssocKind, state);
		if (!state.isKindActive(FadeKind.ENABLE)) {
			return colorScheme.getForegroundColor();
		}

		SubstanceColorScheme prevColorScheme = colorScheme;
		if (prevState != state) {
			ColorSchemeAssociationKind prevAssocKind = ColorSchemeAssociationKind.FILL;
			// use HIGHLIGHT on active menu items
			if (isMenuItem && prevState.isKindActive(FadeKind.ENABLE)
					&& (prevState != ComponentState.DEFAULT))
				prevAssocKind = ColorSchemeAssociationKind.HIGHLIGHT;
			prevColorScheme = SubstanceColorSchemeUtilities.getColorScheme(
					component, prevAssocKind, prevState);
		}

		if (isMenuItem) {
			return SubstanceColorUtilities.getInterpolatedForegroundColor(
					component, null, colorScheme, state, prevColorScheme,
					prevState, FadeKind.ROLLOVER, FadeKind.SELECTION,
					FadeKind.PRESS, FadeKind.ARM);
		} else {
			return SubstanceColorUtilities.getInterpolatedForegroundColor(
					component, null, colorScheme, state, prevColorScheme,
					prevState, FadeKind.ROLLOVER, FadeKind.SELECTION,
					FadeKind.PRESS);
		}
	}

	/**
	 * Returns the foreground text color of the specified tabbed pane tab.
	 * 
	 * @param tabPane
	 *            Tabbed pane.
	 * @param tabIndex
	 *            Tab index.
	 * @param state
	 *            Component current state.
	 * @param prevState
	 *            Component previous state.
	 * @return The foreground text color of the specified tabbed pane tab.
	 */
	public static Color getForegroundColor(JTabbedPane tabPane, int tabIndex,
			ComponentState state, ComponentState prevState) {
		SubstanceColorScheme colorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(tabPane, tabIndex,
						ColorSchemeAssociationKind.TAB, state);
		SubstanceColorScheme prevColorScheme = (prevState != state) ? SubstanceColorSchemeUtilities
				.getColorScheme(tabPane, tabIndex,
						ColorSchemeAssociationKind.TAB, prevState)
				: colorScheme;
		return SubstanceColorUtilities.getInterpolatedForegroundColor(tabPane,
				tabIndex, colorScheme, state, prevColorScheme, prevState,
				FadeKind.ROLLOVER, FadeKind.SELECTION, FadeKind.PRESS);
	}

	/**
	 * Returns the foreground color for the specified component.
	 * 
	 * @param comp
	 *            Component.
	 * @param componentId
	 *            Optional component ID. Can be used to differentiate sub-parts
	 *            of the component, such as tabs in tabbed pane, cells in list
	 *            etc.
	 * @param colorScheme
	 *            Component color scheme.
	 * @param state
	 *            Component current state.
	 * @param prevState
	 *            Component previous state.
	 * @param kinds
	 *            Animation kinds to consult for computing the foreground color.
	 * @return Foreground color.
	 */
	public static Color getInterpolatedForegroundColor(Component comp,
			Comparable<?> componentId, SubstanceColorScheme colorScheme,
			ComponentState state, SubstanceColorScheme prevColorScheme,
			ComponentState prevState, FadeKind... kinds) {
		// SubstanceColorScheme colorScheme2 = colorScheme;
		float cyclePos = state.getCyclePosition();

		FadeState fadeState = SubstanceFadeUtilities.getFadeState(comp,
				componentId, kinds);
		if (fadeState != null) {
			cyclePos = fadeState.getFadePosition();
			if (!fadeState.isFadingIn())
				cyclePos = 1.0f - cyclePos;
		}

		// System.out.println(colorScheme.getDisplayName() + ":"
		// + prevColorScheme.getDisplayName() + ":" + cyclePos);
		Color c1 = colorScheme.getForegroundColor();
		Color c2 = prevColorScheme.getForegroundColor();

		return getInterpolatedColor(c1, c2, cyclePos);
	}

	/**
	 * Returns the background fill color of the specified component.
	 * 
	 * @param component
	 *            Component.
	 * @return The background fill color of the specified component.
	 */
	public static Color getBackgroundFillColor(Component component) {
		// special case - sliders, check boxes and radio buttons. For this,
		// switch to component parent
		// boolean isColorized = hasColorization(component);
		if ((component instanceof JCheckBox)
				|| (component instanceof JRadioButton)
				|| (component instanceof JSlider)) {
			component = component.getParent();
		} else {
			// Fix for 325 - respect the opacity setting of the text
			// component
			if (component instanceof JTextComponent && !component.isOpaque())
				component = component.getParent();
		}

		Color backgr = component.getBackground();
		// do not change the background color on cell
		// renderers
		if (SwingUtilities
				.getAncestorOfClass(CellRendererPane.class, component) != null)
			return backgr;

		boolean isBackgroundUiResource = backgr instanceof UIResource;

		if (!isBackgroundUiResource) {
			// special case for issue 386 - if the colorization
			// is 1.0, return the component background
			if ((SubstanceCoreUtilities.getColorizationFactor(component) == 1.0f)
					&& component.isEnabled()) {
				return backgr;
			}

			SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
					.getColorScheme(component,
							component.isEnabled() ? ComponentState.DEFAULT
									: ComponentState.DISABLED_UNSELECTED);
			backgr = scheme.getBackgroundFillColor();
		} else {
			ComponentState state = component.isEnabled() ? ComponentState.DEFAULT
					: ComponentState.DISABLED_UNSELECTED;
			if (component instanceof JTextComponent) {
				// special case - enabled uneditable text components
				if (component.isEnabled()
						&& !((JTextComponent) component).isEditable()) {
					state = ComponentState.DISABLED_SELECTED;
				}
			}
			// menu items always use the same background color so that the
			// menu looks continuous
			if (component instanceof JMenuItem) {
				state = ComponentState.DEFAULT;
			}
			backgr = SubstanceColorUtilities.getDefaultBackgroundColor(
					component, state);

			if (!state.isKindActive(FadeKind.ENABLE)) {
				float alpha = SubstanceColorSchemeUtilities.getAlpha(component,
						state);
				if (alpha < 1.0f) {
					Color defaultColor = SubstanceColorUtilities
							.getDefaultBackgroundColor(component,
									ComponentState.DEFAULT);
					backgr = SubstanceColorUtilities.getInterpolatedColor(
							backgr, defaultColor, 1.0f - (1.0f - alpha) / 2.0f);
				}
			}
		}
		return backgr;
	}

	/**
	 * Returns the default background color for the components of the specified
	 * class.
	 * 
	 * @param componentClass
	 *            Component class.
	 * @param skin
	 *            Skin.
	 * @param isDisabled
	 *            Indication whether the result should be for disabled
	 *            components.
	 * @return The default background color for the components of the specified
	 *         class.
	 */
	public static ColorUIResource getDefaultBackgroundColor(
			Class<?> componentClass, SubstanceSkin skin, boolean isDisabled) {
		boolean isTextControl = (JTextComponent.class
				.isAssignableFrom(componentClass))
				|| (JComboBox.class.isAssignableFrom(componentClass))
				|| (JSpinner.class.isAssignableFrom(componentClass));
		if (isTextControl || isDisabled)
			return new ColorUIResource(skin.getMainDefaultColorScheme()
					.getTextBackgroundFillColor());
		return new ColorUIResource(skin.getMainDefaultColorScheme()
				.getBackgroundFillColor());
	}

	/**
	 * Returns the default background color for the specified component.
	 * 
	 * @param comp
	 *            Component.
	 * @param compState
	 *            Component state.
	 * @return The default background color for the components of the specified
	 *         class.
	 */
	public static ColorUIResource getDefaultBackgroundColor(Component comp,
			ComponentState compState) {
		// SubstanceSkin skin = SubstanceCoreUtilities.getSkin(comp);
		if ((comp instanceof JTextComponent) || (comp instanceof JComboBox)
				|| (comp instanceof JSpinner)) {
			// special case for text-based components - use lighter
			// background fill color.
			return new ColorUIResource(SubstanceColorSchemeUtilities
					.getColorScheme(comp, compState)
					.getTextBackgroundFillColor());
		}
		return new ColorUIResource(SubstanceCoreUtilities.getSkin(comp)
				.getBackgroundColorScheme(
						SubstanceLookAndFeel.getDecorationType(comp))
				.getBackgroundFillColor());
		// SubstanceColorSchemeUtilities
		// .getColorScheme(comp, compState).getBackgroundFillColor());
	}

	/**
	 * Returns the striped background for the specified component. This method
	 * is relevant for components such as trees, tables and lists that use
	 * odd-even striping for the alternating rows.
	 * 
	 * @param component
	 *            Component.
	 * @param rowIndex
	 *            Row index.
	 * @return The striped background for the specified component.
	 */
	public static Color getStripedBackground(JComponent component, int rowIndex) {
		Color backgr = getBackgroundFillColor(component);
		if (backgr == null)
			return null;

		if (rowIndex % 2 == 0)
			return backgr;
		int r = backgr.getRed();
		int g = backgr.getGreen();
		int b = backgr.getBlue();
		double coef = 0.96;
		if (!component.isEnabled())
			coef = 1.0 - (1.0 - coef) / 2.0;
		Color darkerColor = new ColorUIResource((int) (coef * r),
				(int) (coef * g), (int) (coef * b));

		return darkerColor;
	}

	public static String encode(int number) {
		if ((number < 0) || (number > 255))
			throw new IllegalArgumentException("" + number);
		String hex = "0123456789ABCDEF";
		char c1 = hex.charAt(number / 16);
		char c2 = hex.charAt(number % 16);
		return c1 + "" + c2;
	}

	public static String encode(Color color) {
		return "#" + encode(color.getRed()) + encode(color.getGreen())
				+ encode(color.getBlue());
	}
}
