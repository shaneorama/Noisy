package test.contrib;
import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.image.BufferedImage;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.painter.gradient.BaseGradientPainter;
import org.jvnet.substance.utils.SubstanceColorUtilities;
import org.jvnet.substance.utils.SubstanceCoreUtilities;

public class MyGradientPainter extends BaseGradientPainter
{
	public String getDisplayName()
	{
		return "subdue antialias";
	}

	public void paintContourBackground(Graphics g, Component comp, int width, int height, Shape contour, boolean isFocused, SubstanceColorScheme colorScheme1,
			SubstanceColorScheme colorScheme2, float cyclePos, boolean hasShine, boolean useCyclePosAsInterpolation)
	{
		Graphics2D graphics = (Graphics2D) g.create();

		SubstanceColorScheme interpolationScheme1 = colorScheme1;
		SubstanceColorScheme interpolationScheme2 = useCyclePosAsInterpolation ? colorScheme2 : colorScheme1;

		double cycleCoef = 1.0 - cyclePos;

		Color topFillColor = this.getTopFillColor(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
		Color midFillColorTop = this.getMidFillColorTop(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
		Color midFillColorBottom = this.getMidFillColorBottom(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
		Color bottomFillColor = this.getBottomFillColor(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
		Color topShineColor = this.getTopShineColor(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
		Color bottomShineColor = this.getBottomShineColor(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);

		// Fill background
		// graphics.setClip(contour); old code
		MultipleGradientPaint gradient = new LinearGradientPaint(0, 0, 0, height, new float[]
		{ 0.0f, 0.4999999f, 0.5f, 1.0f }, new Color[]
		{ topFillColor, midFillColorTop, midFillColorBottom, bottomFillColor }, CycleMethod.REPEAT);
		graphics.setPaint(gradient);
		graphics.fill(contour);
		graphics.setClip(contour);// fix for antialias

		if (hasShine && (topShineColor != null) && (bottomShineColor != null))
		{
			int shineHeight = (int) (height / 1.8);
			int kernelSize = (int) Math.min(12, Math.pow(Math.min(width, height), 0.8) / 4);
			if (kernelSize < 3)
				kernelSize = 3;

			BufferedImage blurredGhostContour = SubstanceCoreUtilities.getBlankImage(width + 2 * kernelSize, height + 2 * kernelSize);
			Graphics2D blurredGhostGraphics = (Graphics2D) blurredGhostContour.getGraphics().create();
			blurredGhostGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			blurredGhostGraphics.setColor(Color.black);
			blurredGhostGraphics.translate(kernelSize, kernelSize);
			int step = kernelSize > 5 ? 2 : 1;
			for (int strokeSize = 2 * kernelSize - 1; strokeSize > 0; strokeSize -= step)
			{
				float transp = 1.0f - strokeSize / (2.0f * kernelSize);
				blurredGhostGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, transp));
				blurredGhostGraphics.setStroke(new BasicStroke(strokeSize));
				blurredGhostGraphics.draw(contour);
			}
			blurredGhostGraphics.dispose();

			BufferedImage reverseGhostContour = SubstanceCoreUtilities.getBlankImage(width + 2 * kernelSize, height + 2 * kernelSize);
			Graphics2D reverseGraphics = (Graphics2D) reverseGhostContour.getGraphics();
			Color bottomShineColorTransp = new Color(bottomShineColor.getRed(), bottomShineColor.getGreen(), bottomShineColor.getBlue(), 64);
			GradientPaint gradientShine = new GradientPaint(0, kernelSize, topShineColor, 0, kernelSize + shineHeight, bottomShineColorTransp);
			reverseGraphics.setPaint(gradientShine);
			reverseGraphics.fillRect(0, kernelSize, width + 2 * kernelSize, kernelSize + shineHeight);
			reverseGraphics.setComposite(AlphaComposite.DstOut);
			reverseGraphics.drawImage(blurredGhostContour, 0, 0, null);

			graphics.drawImage(reverseGhostContour, 0, 0, width - 1, shineHeight, kernelSize, kernelSize, kernelSize + width - 1, kernelSize + shineHeight,
					null);
		}

		graphics.dispose();
	}

	public Color getTopFillColor(SubstanceColorScheme interpolationScheme1, SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation)
	{
		return SubstanceColorUtilities.getTopFillColor(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	public Color getMidFillColorTop(SubstanceColorScheme interpolationScheme1, SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation)
	{

		return SubstanceColorUtilities.getMidFillColor(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	public Color getMidFillColorBottom(SubstanceColorScheme interpolationScheme1, SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation)
	{

		return this.getMidFillColorTop(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	public Color getBottomFillColor(SubstanceColorScheme interpolationScheme1, SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation)
	{
		return SubstanceColorUtilities.getBottomFillColor(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	public Color getTopShineColor(SubstanceColorScheme interpolationScheme1, SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation)
	{
		return SubstanceColorUtilities.getTopShineColor(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}

	public Color getBottomShineColor(SubstanceColorScheme interpolationScheme1, SubstanceColorScheme interpolationScheme2, double cycleCoef,
			boolean useCyclePosAsInterpolation)
	{
		return SubstanceColorUtilities.getBottomShineColor(interpolationScheme1, interpolationScheme2, cycleCoef, useCyclePosAsInterpolation);
	}
}