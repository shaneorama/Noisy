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
package org.jvnet.substance.painter.gradient;

import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.image.BufferedImage;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.utils.SubstanceColorUtilities;
import org.jvnet.substance.utils.SubstanceCoreUtilities;

/**
 * Gradient painter that returns images with subtle 3D gradient appearance. This
 * class is part of officially supported API.
 * 
 * @author Kirill Grouchnikov
 */
public class StandardGradientPainter extends BaseGradientPainter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.painter.SubstanceGradientPainter#getDisplayName()
	 */
	public String getDisplayName() {
		return "Standard";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jvnet.substance.painter.gradient.SubstanceGradientPainter#
	 * paintContourBackground(java.awt.Graphics, java.awt.Component, int, int,
	 * java.awt.Shape, boolean, org.jvnet.substance.api.SubstanceColorScheme,
	 * org.jvnet.substance.api.SubstanceColorScheme, float, boolean, boolean)
	 */
	public void paintContourBackground(Graphics g, Component comp, int width,
			int height, Shape contour, boolean isFocused,
			SubstanceColorScheme colorScheme1,
			SubstanceColorScheme colorScheme2, float cyclePos,
			boolean hasShine, boolean useCyclePosAsInterpolation) {

		// long millis = System.nanoTime();

		Graphics2D graphics = (Graphics2D) g.create();

		SubstanceColorScheme interpolationScheme1 = colorScheme1;
		SubstanceColorScheme interpolationScheme2 = useCyclePosAsInterpolation ? colorScheme2
				: colorScheme1;

		double cycleCoef = 1.0 - cyclePos;

		Color topFillColor = this.getTopFillColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
		Color midFillColorTop = this.getMidFillColorTop(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
		Color midFillColorBottom = this.getMidFillColorBottom(
				interpolationScheme1, interpolationScheme2, cycleCoef,
				useCyclePosAsInterpolation);
		Color bottomFillColor = this.getBottomFillColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
		Color topShineColor = this.getTopShineColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
		Color bottomShineColor = this.getBottomShineColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);

		// Fill background
		// long millis000 = System.nanoTime();

		//graphics.clip(contour);
		MultipleGradientPaint gradient = new LinearGradientPaint(0, 0, 0,
				height, new float[] { 0.0f, 0.4999999f, 0.5f, 1.0f },
				new Color[] { topFillColor, midFillColorTop,
						midFillColorBottom, bottomFillColor },
				CycleMethod.REPEAT);
		graphics.setPaint(gradient);
		graphics.fill(contour);

		// long millis003 = 0, millis004 = 0, millis005 = 0;
		if (hasShine && (topShineColor != null) && (bottomShineColor != null)) {
			graphics.clip(contour);
			int shineHeight = (int) (height / 1.8);
			int kernelSize = (int) Math.min(12, Math.pow(Math
					.min(width, height), 0.8) / 4);
			if (kernelSize < 3)
				kernelSize = 3;

			BufferedImage blurredGhostContour = SubstanceCoreUtilities
					.getBlankImage(width + 2 * kernelSize, height + 2
							* kernelSize);
			Graphics2D blurredGhostGraphics = (Graphics2D) blurredGhostContour
					.getGraphics().create();
			blurredGhostGraphics.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			blurredGhostGraphics.setColor(Color.black);
			blurredGhostGraphics.translate(kernelSize, kernelSize);
			int step = kernelSize > 5 ? 2 : 1;
			for (int strokeSize = 2 * kernelSize - 1; strokeSize > 0; strokeSize -= step) {
				float transp = 1.0f - strokeSize / (2.0f * kernelSize);
				blurredGhostGraphics.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC, transp));
				blurredGhostGraphics.setStroke(new BasicStroke(strokeSize));
				blurredGhostGraphics.draw(contour);
			}
			blurredGhostGraphics.dispose();

			// millis003 = System.nanoTime();

			BufferedImage reverseGhostContour = SubstanceCoreUtilities
					.getBlankImage(width + 2 * kernelSize, height + 2
							* kernelSize);
			Graphics2D reverseGraphics = (Graphics2D) reverseGhostContour
					.getGraphics();
			Color bottomShineColorTransp = new Color(bottomShineColor.getRed(),
					bottomShineColor.getGreen(), bottomShineColor.getBlue(), 64);
			GradientPaint gradientShine = new GradientPaint(0, kernelSize,
					topShineColor, 0, kernelSize + shineHeight,
					bottomShineColorTransp);
			reverseGraphics.setPaint(gradientShine);
			reverseGraphics.fillRect(0, kernelSize, width + 2 * kernelSize,
					kernelSize + shineHeight);
			reverseGraphics.setComposite(AlphaComposite.DstOut);
			reverseGraphics.drawImage(blurredGhostContour, 0, 0, null);
			// millis004 = System.nanoTime();

			graphics.drawImage(reverseGhostContour, 0, 0, width - 1,
					shineHeight, kernelSize, kernelSize,
					kernelSize + width - 1, kernelSize + shineHeight, null);

			BufferedImage overGhostContour = SubstanceCoreUtilities
					.getBlankImage(width + 2 * kernelSize, height + 2
							* kernelSize);
			Graphics2D overGraphics = (Graphics2D) overGhostContour
					.getGraphics();
			overGraphics.setPaint(new GradientPaint(0, kernelSize,
					topFillColor, 0, kernelSize + height / 2, midFillColorTop));
			overGraphics.fillRect(kernelSize, kernelSize, kernelSize + width,
					kernelSize + shineHeight);
			overGraphics.setComposite(AlphaComposite.DstIn);
			overGraphics.drawImage(blurredGhostContour, 0, 0, null);
			// millis005 = System.nanoTime();

			graphics.drawImage(overGhostContour, 0, 0, width - 1, shineHeight,
					kernelSize, kernelSize, kernelSize + width - 1, kernelSize
							+ shineHeight, null);
		}

		graphics.dispose();
		// long millis006 = System.nanoTime();

		// long millis2 = System.nanoTime();
		// if (width * height > 5000) {
		// System.out.println("new - " + width + "*" + height + " = "
		// + format(millis2 - millis));
		// System.out.println("\tfill : " + format(millis001 - millis000));
		// System.out.println("\tcontour : " + format(millis003 - millis001));
		// System.out.println("\trevert : " + format(millis004 - millis003));
		// System.out.println("\toverlay : " + format(millis005 - millis004));
		// System.out.println("\tborder : " + format(millis006 - millis005));
		// }
	}

	// /**
	// * Computes the color of the top portion of the border. Override to
	// provide
	// * different visual.
	// *
	// * @param interpolationScheme1
	// * The first interpolation scheme.
	// * @param interpolationScheme2
	// * The second interpolation scheme.
	// * @param cycleCoef
	// * Cycle coefficient.
	// * @param useCyclePosAsInterpolation
	// * Indicates whether the cycle coefficient should be used as the
	// * interpolation parameter.
	// * @return The color of the top portion of the border.
	// */
	// public Color getTopBorderColor(ColorScheme interpolationScheme1,
	// ColorScheme interpolationScheme2, double cycleCoef,
	// boolean useCyclePosAsInterpolation) {
	// return SubstanceColorUtilities.getTopBorderColor(interpolationScheme1,
	// interpolationScheme2, cycleCoef);
	// }
	//
	// /**
	// * Computes the color of the middle portion of the border. Override to
	// * provide different visual.
	// *
	// * @param interpolationScheme1
	// * The first interpolation scheme.
	// * @param interpolationScheme2
	// * The second interpolation scheme.
	// * @param cycleCoef
	// * Cycle coefficient.
	// * @param useCyclePosAsInterpolation
	// * Indicates whether the cycle coefficient should be used as the
	// * interpolation parameter.
	// * @return The color of the middle portion of the border.
	// */
	// public Color getMidBorderColor(ColorScheme interpolationScheme1,
	// ColorScheme interpolationScheme2, double cycleCoef,
	// boolean useCyclePosAsInterpolation) {
	// return SubstanceColorUtilities.getMidBorderColor(interpolationScheme1,
	// interpolationScheme2, cycleCoef);
	// }
	//
	// /**
	// * Computes the color of the bottom portion of the border. Override to
	// * provide different visual.
	// *
	// * @param interpolationScheme1
	// * The first interpolation scheme.
	// * @param interpolationScheme2
	// * The second interpolation scheme.
	// * @param cycleCoef
	// * Cycle coefficient.
	// * @param useCyclePosAsInterpolation
	// * Indicates whether the cycle coefficient should be used as the
	// * interpolation parameter.
	// * @return The color of the bottom portion of the border.
	// */
	// public Color getBottomBorderColor(ColorScheme interpolationScheme1,
	// ColorScheme interpolationScheme2, double cycleCoef,
	// boolean useCyclePosAsInterpolation) {
	// return SubstanceColorUtilities.getBottomBorderColor(
	// interpolationScheme1, interpolationScheme2, cycleCoef);
	// }

	/**
	 * Computes the color of the top portion of the fill. Override to provide
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
	 * @return The color of the top portion of the fill.
	 */
	public Color getTopFillColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return SubstanceColorUtilities.getTopFillColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	/**
	 * Computes the color of the middle portion of the fill from the top.
	 * Override to provide different visual.
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
	 * @return The color of the middle portion of the fill from the top.
	 */
	public Color getMidFillColorTop(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {

		return SubstanceColorUtilities.getMidFillColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	/**
	 * Computes the color of the middle portion of the fill from the bottom.
	 * Override to provide different visual.
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
	 * @return The color of the middle portion of the fill from the bottom.
	 */
	public Color getMidFillColorBottom(
			SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {

		return this.getMidFillColorTop(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	/**
	 * Computes the color of the bottom portion of the fill. Override to provide
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
	 * @return The color of the bottom portion of the fill.
	 */
	public Color getBottomFillColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return SubstanceColorUtilities.getBottomFillColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	/**
	 * Computes the color of the top portion of the shine. Override to provide
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
	 * @return The color of the top portion of the shine.
	 */
	public Color getTopShineColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return SubstanceColorUtilities.getTopShineColor(interpolationScheme1,
				interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	/**
	 * Computes the color of the bottom portion of the shine. Override to
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
	 * @return The color of the bottom portion of the shine.
	 */
	public Color getBottomShineColor(SubstanceColorScheme interpolationScheme1,
			SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation) {
		return SubstanceColorUtilities.getBottomShineColor(
				interpolationScheme1, interpolationScheme2, cycleCoef,
				useCyclePosAsInterpolation);
	}
}