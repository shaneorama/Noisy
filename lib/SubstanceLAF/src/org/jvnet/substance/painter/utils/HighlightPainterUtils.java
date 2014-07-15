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
package org.jvnet.substance.painter.utils;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.Set;

import javax.swing.CellRendererPane;

import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.api.*;
import org.jvnet.substance.api.SubstanceConstants.Side;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.highlight.SubstanceHighlightPainter;
import org.jvnet.substance.utils.*;

/**
 * Contains utility methods related to highlight painters. This class is for
 * internal use only.
 * 
 * @author Kirill Grouchnikov
 */
public class HighlightPainterUtils {
	/**
	 * Cache for small objects.
	 */
	protected final static LazyResettableHashMap<BufferedImage> smallCache = new LazyResettableHashMap<BufferedImage>(
			"SubstanceHighlightUtils");

	/**
	 * Updates the specified component with the background that matches the
	 * provided parameters.
	 * 
	 * @param g
	 *            Graphic context.
	 * @param rendererPane
	 *            Renderer pane. Can be <code>null</code>.
	 * @param c
	 *            Component.
	 * @param rect
	 *            Rectangle to highlight.
	 * @param borderAlpha
	 *            Border alpha.
	 * @param openSides
	 *            The sides specified in this set will not be painted. Can be
	 *            <code>null</code> or empty.
	 * @param currState
	 *            The current state of the component.
	 * @param prevState
	 *            The previous state of the component.
	 * @param cyclePos
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0..1 range.
	 */
	public static void paintHighlight(Graphics g,
			CellRendererPane rendererPane, Component c, Rectangle rect,
			float borderAlpha, Set<Side> openSides, ComponentState currState,
			ComponentState prevState, float cyclePos) {
		// fix for bug 65
		if ((rect.width <= 0) || (rect.height <= 0))
			return;

		// fix for issue 442 - use renderer pane for computing the
		// color schemes when necessary
		Component compForQuerying = (rendererPane != null) ? rendererPane : c;
		SubstanceColorScheme currScheme = SubstanceColorSchemeUtilities
				.getColorScheme(compForQuerying,
						ColorSchemeAssociationKind.HIGHLIGHT, currState);
		SubstanceColorScheme prevScheme = (cyclePos > 0.0f) ? SubstanceColorSchemeUtilities
				.getColorScheme(compForQuerying,
						ColorSchemeAssociationKind.HIGHLIGHT, prevState)
				: currScheme;

		SubstanceColorScheme currBorderScheme = SubstanceColorSchemeUtilities
				.getColorScheme(compForQuerying,
						ColorSchemeAssociationKind.HIGHLIGHT_BORDER, currState);
		SubstanceColorScheme prevBorderScheme = (cyclePos > 0.0f) ? SubstanceColorSchemeUtilities
				.getColorScheme(compForQuerying,
						ColorSchemeAssociationKind.HIGHLIGHT_BORDER, prevState)
				: currBorderScheme;

		// if (c instanceof JMenu) {
		// System.out.println(((JMenu) c).getText() + ":" + prevState + " ["
		// + prevScheme.getDisplayName() + "] -> " + currState + " ["
		// + currScheme.getDisplayName() + "]");
		// }
		paintHighlight(g, rendererPane, c, rect, borderAlpha, openSides,
				currScheme, prevScheme, currBorderScheme, prevBorderScheme,
				cyclePos);
	}

	/**
	 * Paints the highlight for the specified component.
	 * 
	 * @param g
	 *            Graphic context.
	 * @param rendererPane
	 *            Renderer pane. Can be <code>null</code>.
	 * @param c
	 *            Component.
	 * @param rect
	 *            Rectangle to highlight.
	 * @param borderAlpha
	 *            Border alpha.
	 * @param openSides
	 *            The sides specified in this set will not be painted. Can be
	 *            <code>null</code> or empty.
	 * @param currScheme
	 *            The first color scheme.
	 * @param prevScheme
	 *            The second color scheme.
	 * @param currBorderScheme
	 *            The first border color scheme.
	 * @param prevBorderScheme
	 *            The second border color scheme.
	 * @param cyclePos
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0..1 range.
	 */
	public static void paintHighlight(Graphics g,
			CellRendererPane rendererPane, Component c, Rectangle rect,
			float borderAlpha, Set<Side> openSides,
			SubstanceColorScheme currScheme, SubstanceColorScheme prevScheme,
			SubstanceColorScheme currBorderScheme,
			SubstanceColorScheme prevBorderScheme, float cyclePos) {
		// fix for bug 65
		if ((rect.width <= 0) || (rect.height <= 0))
			return;

		Component compForQuerying = (rendererPane != null) ? rendererPane : c;
		SubstanceHighlightPainter highlightPainter = SubstanceCoreUtilities
				.getSkin(compForQuerying).getHighlightPainter();
		SubstanceBorderPainter highlightBorderPainter = SubstanceCoreUtilities
				.getHighlightBorderPainter(compForQuerying);
		Graphics2D g2d = (Graphics2D) g.create(rect.x, rect.y, rect.width,
				rect.height);

		if (openSides == null) {
			openSides = EnumSet.noneOf(Side.class);
		}
		if (rect.width * rect.height < 100000) {
			String openKey = "";
			for (Side oSide : openSides) {
				openKey += oSide.name() + "-";
			}

			HashMapKey key = SubstanceCoreUtilities.getHashKey(highlightPainter
					.getDisplayName(), highlightBorderPainter.getDisplayName(),
					rect.width, rect.height, currScheme.getDisplayName(),
					prevScheme.getDisplayName(), currBorderScheme
							.getDisplayName(), prevBorderScheme
							.getDisplayName(), cyclePos, borderAlpha, openKey);
			BufferedImage result = smallCache.get(key);
			if (result == null) {
				// System.out.println("Cache miss");
				result = SubstanceCoreUtilities.getBlankImage(rect.width,
						rect.height);
				Graphics2D resGraphics = result.createGraphics();
				highlightPainter.paintHighlight(resGraphics, c, rect.width,
						rect.height, openSides, currScheme, prevScheme,
						cyclePos);
				paintHighlightBorder(resGraphics, c, rect.width, rect.height,
						borderAlpha, openSides, highlightBorderPainter,
						currBorderScheme, prevBorderScheme, cyclePos);
				resGraphics.dispose();
				smallCache.put(key, result);
			} else {
				// System.out.println("Cache hit");
			}
			g2d.drawImage(result, 0, 0, null);
			return;
		}

		highlightPainter.paintHighlight(g2d, c, rect.width, rect.height,
				openSides, currScheme, prevScheme, cyclePos);
		paintHighlightBorder(g2d, c, rect.width, rect.height, borderAlpha,
				openSides, highlightBorderPainter, currBorderScheme,
				prevBorderScheme, cyclePos);

		g2d.dispose();
	}

	/**
	 * Paints the highlight border for the specified component.
	 * 
	 * @param graphics
	 *            Graphic context.
	 * @param comp
	 *            Component.
	 * @param width
	 *            Border width.
	 * @param height
	 *            Border width.
	 * @param borderAlpha
	 *            Border alpha.
	 * @param openSides
	 *            The sides specified in this set will not be painted. Can be
	 *            <code>null</code> or empty.
	 * @param highlightBorderPainter
	 *            Border painter for the highlights.
	 * @param borderColorScheme1
	 *            The first border color scheme.
	 * @param borderColorScheme2
	 *            The second border color scheme.
	 * @param cyclePos
	 *            Cycle position. Is used for rollover and pulsation effects.
	 *            Must be in 0..1 range.
	 */
	private static void paintHighlightBorder(Graphics2D graphics,
			Component comp, int width, int height, float borderAlpha,
			Set<Side> openSides, SubstanceBorderPainter highlightBorderPainter,
			SubstanceColorScheme borderColorScheme1,
			SubstanceColorScheme borderColorScheme2, float cyclePos) {
		if (borderAlpha <= 0.0f)
			return;

		int openDelta = 3 + (int) (Math.ceil(3.0 * SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(comp))));
		int deltaLeft = openSides.contains(Side.LEFT) ? openDelta : 0;
		int deltaRight = openSides.contains(Side.RIGHT) ? openDelta : 0;
		int deltaTop = openSides.contains(Side.TOP) ? openDelta : 0;
		int deltaBottom = openSides.contains(Side.BOTTOM) ? openDelta : 0;

		int borderDelta = (int) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(comp)) / 2.0);
		GeneralPath contour = SubstanceOutlineUtilities.getBaseOutline(width
				+ deltaLeft + deltaRight, height + deltaTop + deltaBottom,
				0.0f, null, borderDelta);

		Graphics2D g2d = (Graphics2D) graphics.create();
		g2d.translate(-deltaLeft, -deltaTop);
		g2d.setComposite(TransitionLayout.getAlphaComposite(null, borderAlpha,
				graphics));
		int borderThickness = (int) SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(comp));
		GeneralPath contourInner = SubstanceOutlineUtilities.getBaseOutline(
				width + deltaLeft + deltaRight,
				height + deltaTop + deltaBottom, 0.0f, null, borderDelta
						+ borderThickness);
		highlightBorderPainter.paintBorder(g2d, comp, width + deltaLeft
				+ deltaRight, height + deltaTop + deltaBottom, contour,
				contourInner, borderColorScheme1, borderColorScheme2, cyclePos,
				true);
		g2d.dispose();
	}

	/**
	 * Returns the memory usage string.
	 * 
	 * @return Memory usage string.
	 */
	public static String getMemoryUsage() {
		StringBuffer sb = new StringBuffer();
		sb.append("SubstanceHighlightUtils: \n");
		sb.append("\t" + smallCache.size() + " smalls");
		return sb.toString();
	}
}
