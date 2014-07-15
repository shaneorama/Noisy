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

import javax.swing.CellRendererPane;
import javax.swing.JComponent;

import org.jvnet.lafwidget.LafWidgetUtilities;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.utils.SubstanceColorUtilities;
import org.jvnet.substance.utils.SubstanceCoreUtilities;
import org.jvnet.substance.watermark.SubstanceWatermark;

/**
 * Delegate for painting filled backgrounds.
 * 
 * @author Kirill Grouchnikov
 */
public class BackgroundPaintingUtils {
	/**
	 * Updates the background of the specified component on the specified
	 * graphic context. The background is updated only if the component is
	 * opaque.
	 * 
	 * @param g
	 *            Graphic context.
	 * @param c
	 *            Component.
	 */
	public static void updateIfOpaque(Graphics g, Component c) {
		if (TransitionLayout.isOpaque(c))
			update(g, c, false);
	}

	/**
	 * Updates the background of the specified component on the specified
	 * graphic context. The background is not painted when the
	 * <code>force</code> parameter is <code>false</code> and at least one of
	 * the following conditions holds:
	 * <ul>
	 * <li>The component is in a cell renderer.</li>
	 * <li>The component is not showing on the screen.</li>
	 * <li>The component is in the preview mode.</li>
	 * </ul>
	 * 
	 * @param g
	 *            Graphic context.
	 * @param c
	 *            Component.
	 * @param force
	 *            If <code>true</code>, the painting of background is enforced.
	 */
	public static void update(Graphics g, Component c, boolean force) {
		// failsafe for LAF change
		if (!SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			return;
		}

		boolean isInCellRenderer = (c.getParent() instanceof CellRendererPane);
		boolean isPreviewMode = false;
		if (c instanceof JComponent) {
			isPreviewMode = (Boolean.TRUE.equals(((JComponent) c)
					.getClientProperty(LafWidgetUtilities.PREVIEW_MODE)));
		}
		if (!force && !isPreviewMode && !c.isShowing() && !isInCellRenderer) {
			return;
		}

		Graphics2D graphics = (Graphics2D) g.create();
		// optimization - do not call fillRect on graphics
		// with anti-alias turned on
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		graphics.setComposite(TransitionLayout.getAlphaComposite(c, g));

		DecorationAreaType decorationType = SubstanceLookAndFeel
				.getDecorationType(c);
		SubstanceSkin skin = SubstanceCoreUtilities.getSkin(c);
		if ((decorationType != null)
				&& (skin.isRegisteredAsDecorationArea(decorationType))) {
			// use the decoration painter
			DecorationPainterUtils
					.paintDecorationBackground(graphics, c, force);
			// and add overlays
			OverlayPainterUtils
					.paintOverlays(graphics, c, skin, decorationType);
		} else {
			// fill the area with solid color
			Color backgr = SubstanceColorUtilities.getBackgroundFillColor(c);
			graphics.setColor(backgr);
			graphics.fillRect(0, 0, c.getWidth(), c.getHeight());
			// add overlays
			OverlayPainterUtils
					.paintOverlays(graphics, c, skin, decorationType);
			// boolean isPaintingDropShadows = skin
			// .isPaintingDropShadows(decorationAreaType);
			// if (isPaintingDropShadows) {
			// Color fillColor = skin.getMainActiveColorScheme(
			// decorationAreaType).getBackgroundFillColor().darker();
			//
			// // need to handle components "embedded" in other components
			// int dy = 0;
			// int totalOffsetY = 0;
			// Component comp = c;
			// while (comp.getParent() != null) {
			// Component parent = comp.getParent();
			// dy += comp.getY();
			// if (DecorationPainterUtils
			// .getImmediateDecorationType(parent) != null) {
			// totalOffsetY += dy;
			// dy = 0;
			// }
			// comp = parent;
			// }
			//
			// graphics.translate(0, -totalOffsetY);
			// graphics.setPaint(new GradientPaint(0, 0,
			// SubstanceColorUtilities.getAlphaColor(fillColor, 160),
			// 0, 4, SubstanceColorUtilities.getAlphaColor(fillColor,
			// 16)));
			// graphics.fillRect(0, 0, c.getWidth(), 4);
			// graphics.translate(0, totalOffsetY);
			// }

			// and paint watermark
			SubstanceWatermark watermark = SubstanceCoreUtilities.getSkin(c)
					.getWatermark();
			if ((watermark != null) && !isPreviewMode && !isInCellRenderer
					&& c.isShowing()
					&& SubstanceCoreUtilities.toDrawWatermark(c)) {
				watermark.drawWatermarkImage(graphics, c, 0, 0, c.getWidth(), c
						.getHeight());
			}
		}

		graphics.dispose();
	}

	/**
	 * Updates the background of the specified component on the specified
	 * graphic context in the specified rectangle.
	 * 
	 * @param g
	 *            Graphic context.
	 * @param c
	 *            Component.
	 * @param fillColor
	 *            Fill color.
	 * @param rect
	 *            The rectangle to fill.
	 */
	public static void fillAndWatermark(Graphics g, JComponent c,
			Color fillColor, Rectangle rect) {
		// failsafe for LAF change
		if (!SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			return;
		}

		boolean isInCellRenderer = (c.getParent() instanceof CellRendererPane);
		if ((!c.isShowing()) && (!isInCellRenderer)) {
			return;
		}

		Graphics2D graphics = (Graphics2D) g.create();
		graphics.setComposite(TransitionLayout.getAlphaComposite(c, g));
		graphics.setColor(fillColor);
		graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
		graphics.setComposite(TransitionLayout.getAlphaComposite(c, 1.0f, g));
		// stamp watermark
		SubstanceWatermark watermark = SubstanceCoreUtilities.getSkin(c)
				.getWatermark();
		if ((watermark != null) && !isInCellRenderer && c.isShowing()
				&& SubstanceCoreUtilities.toDrawWatermark(c))
			watermark.drawWatermarkImage(graphics, c, rect.x, rect.y,
					rect.width, rect.height);
		graphics.dispose();
	}
}
