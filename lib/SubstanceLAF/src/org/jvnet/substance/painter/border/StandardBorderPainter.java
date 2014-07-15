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
package org.jvnet.substance.painter.border;

import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.utils.*;

/**
 * The default border painter. This class is part of officially supported API.
 * 
 * @author Kirill Grouchnikov
 */
public class StandardBorderPainter implements SubstanceBorderPainter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.painter.SubstanceGradientPainter#getDisplayName()
	 */
	public String getDisplayName() {
		return "Standard";
	}

	@Override
	public boolean isPaintingInnerContour() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.border.SubstanceBorderPainter#paintBorder(java.awt
	 * .Graphics, java.awt.Component, int, int, java.awt.Shape, java.awt.Shape,
	 * org.jvnet.substance.color.ColorScheme,
	 * org.jvnet.substance.color.ColorScheme, float, boolean)
	 */
	public void paintBorder(Graphics g, Component c, int width, int height,
			Shape contour, Shape innerContour,
			SubstanceColorScheme colorScheme1,
			SubstanceColorScheme colorScheme2, float cyclePos,
			boolean useCyclePosAsInterpolation) {
		if (contour == null)
			return;

		Graphics2D graphics = (Graphics2D) g.create();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_NORMALIZE);

		SubstanceColorScheme interpolationScheme1 = colorScheme1;
		SubstanceColorScheme interpolationScheme2 = useCyclePosAsInterpolation ? colorScheme2
				: colorScheme1;

		double cycleCoef = 1.0 - cyclePos;

		Color topBorderColor = getTopBorderColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
		Color midBorderColor = getMidBorderColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
		Color bottomBorderColor = getBottomBorderColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);

		if ((topBorderColor != null) && (midBorderColor != null)
				&& (bottomBorderColor != null)) {
			float strokeWidth = SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(c));
			// issue 433 - the "c" can be null when painting
			// the border of a tree icon used outside the
			// JTree context.
			boolean isSpecialButton = (c == null) ? false : c.getClass()
					.isAnnotationPresent(SubstanceArrowButton.class);
			int joinKind = isSpecialButton ? BasicStroke.JOIN_MITER
					: BasicStroke.JOIN_ROUND;
			int capKind = isSpecialButton ? BasicStroke.CAP_SQUARE
					: BasicStroke.CAP_BUTT;
			graphics.setStroke(new BasicStroke(strokeWidth, capKind, joinKind));

			MultipleGradientPaint gradient = new LinearGradientPaint(0, 0, 0,
					height, new float[] { 0.0f, 0.5f, 1.0f },
					new Color[] { topBorderColor, midBorderColor,
							bottomBorderColor }, CycleMethod.REPEAT);
			graphics.setPaint(gradient);
			graphics.draw(contour);
		}

		graphics.dispose();
	}

	/**
	 * Computes the color of the top portion of the border. Override to provide
	 * different visual.
	 * 
	 * @param interpolationScheme1
	 *            The first interpolation scheme.
	 * @param interpolationScheme2
	 *            The second interpolation scheme.
	 * @param cycleCoef
	 *            Cycle coefficient.
	 * @param useCyclePosAsInterpolation
	 *            Indicates whether the cycle coefficient should be used as the
	 *            interpolation parameter.
	 * @return The color of the top portion of the border.
	 */
	public Color getTopBorderColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return SubstanceColorUtilities.getTopBorderColor(interpolationScheme1,
				interpolationScheme2, cycleCoef);
	}

	/**
	 * Computes the color of the middle portion of the border. Override to
	 * provide different visual.
	 * 
	 * @param interpolationScheme1
	 *            The first interpolation scheme.
	 * @param interpolationScheme2
	 *            The second interpolation scheme.
	 * @param cycleCoef
	 *            Cycle coefficient.
	 * @param useCyclePosAsInterpolation
	 *            Indicates whether the cycle coefficient should be used as the
	 *            interpolation parameter.
	 * @return The color of the middle portion of the border.
	 */
	public Color getMidBorderColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return SubstanceColorUtilities.getMidBorderColor(interpolationScheme1,
				interpolationScheme2, cycleCoef);
	}

	/**
	 * Computes the color of the bottom portion of the border. Override to
	 * provide different visual.
	 * 
	 * @param interpolationScheme1
	 *            The first interpolation scheme.
	 * @param interpolationScheme2
	 *            The second interpolation scheme.
	 * @param cycleCoef
	 *            Cycle coefficient.
	 * @param useCyclePosAsInterpolation
	 *            Indicates whether the cycle coefficient should be used as the
	 *            interpolation parameter.
	 * @return The color of the bottom portion of the border.
	 */
	public Color getBottomBorderColor(
			SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return SubstanceColorUtilities.getBottomBorderColor(
				interpolationScheme1, interpolationScheme2, cycleCoef);
	}

}
