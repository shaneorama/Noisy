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
package org.jvnet.substance.painter.decoration;

import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.*;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.utils.SubstanceCoreUtilities;

/**
 * Implementation of {@link SubstanceDecorationPainter} that uses "arc" painting
 * on title panes and lighter gradient near the center of the application frame.
 * 
 * @author Kirill Grouchnikov
 */
public class ArcDecorationPainter implements SubstanceDecorationPainter {
	/**
	 * The display name for the decoration painters of this class.
	 */
	public static final String DISPLAY_NAME = "Arc";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.utils.SubstanceTrait#getDisplayName()
	 */
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jvnet.substance.painter.decoration.SubstanceDecorationPainter#
	 * paintDecorationArea(java.awt.Graphics2D, java.awt.Component,
	 * org.jvnet.substance.painter.decoration.DecorationAreaType, int, int,
	 * org.jvnet.substance.skin.SubstanceSkin)
	 */
	public void paintDecorationArea(Graphics2D graphics, Component comp,
			DecorationAreaType decorationAreaType, int width, int height,
			SubstanceSkin skin) {
		switch (decorationAreaType) {
		case PRIMARY_TITLE_PANE:
		case SECONDARY_TITLE_PANE:
			this.paintTitleBackground(graphics, comp, width, height, skin
					.getBackgroundColorScheme(decorationAreaType));
			break;
		default:
			this.paintExtraBackground(graphics, SubstanceCoreUtilities
					.getHeaderParent(comp), comp, width, height, skin
					.getBackgroundColorScheme(decorationAreaType));
		}
	}

	/**
	 * Paints the title background.
	 * 
	 * @param graphics
	 *            Graphics context.
	 * @param comp
	 *            Component.
	 * @param width
	 *            Width.
	 * @param height
	 *            Height.
	 * @param scheme
	 *            Color scheme for painting the title background.
	 */
	private void paintTitleBackground(Graphics2D graphics, Component comp,
			int width, int height, SubstanceColorScheme scheme) {
		// System.out.println(scheme.getDisplayName());
		// create rectangular background and later draw it on
		// result image with contour clip.
		BufferedImage rectangular = SubstanceCoreUtilities.getBlankImage(width,
				height);
		Graphics2D rgraphics = (Graphics2D) rectangular.getGraphics();

		// Fill background
		GeneralPath clipTop = new GeneralPath();
		clipTop.moveTo(0, 0);
		clipTop.lineTo(width, 0);
		clipTop.lineTo(width, height / 2);
		clipTop.quadTo(width / 2, height / 4, 0, height / 2);
		clipTop.lineTo(0, 0);

		rgraphics.setClip(clipTop);
		LinearGradientPaint gradientTop = new LinearGradientPaint(0, 0, width,
				0, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] {
						scheme.getLightColor(), scheme.getUltraLightColor(),
						scheme.getLightColor() }, CycleMethod.REPEAT);
		rgraphics.setPaint(gradientTop);
		rgraphics.fillRect(0, 0, width, height);

		GeneralPath clipBottom = new GeneralPath();
		clipBottom.moveTo(0, height);
		clipBottom.lineTo(width, height);
		clipBottom.lineTo(width, height / 2);
		clipBottom.quadTo(width / 2, height / 4, 0, height / 2);
		clipBottom.lineTo(0, height);

		rgraphics.setClip(clipBottom);
		LinearGradientPaint gradientBottom = new LinearGradientPaint(0, 0,
				width, 0, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] {
						scheme.getMidColor(), scheme.getLightColor(),
						scheme.getMidColor() }, CycleMethod.REPEAT);
		rgraphics.setPaint(gradientBottom);
		rgraphics.fillRect(0, 0, width, height);

		GeneralPath mid = new GeneralPath();
		mid.moveTo(width, height / 2);
		mid.quadTo(width / 2, height / 4, 0, height / 2);
		// rgraphics.setClip(new Rectangle(0, 0, width / 2, height));
		// rgraphics
		// .setClip(new Rectangle(width / 2, 0, width - width / 2, height));
		rgraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		rgraphics.setClip(new Rectangle(0, 0, width, height));
		rgraphics.draw(mid);

		graphics.drawImage(rectangular, 0, 0, null);
	}

	/**
	 * Paints the background of non-title decoration areas.
	 * 
	 * @param graphics
	 *            Graphics context.
	 * @param parent
	 *            Component ancestor for computing the correct offset of the
	 *            background painting.
	 * @param comp
	 *            Component.
	 * @param width
	 *            Width.
	 * @param height
	 *            Height.
	 * @param scheme
	 *            Color scheme for painting the title background.
	 */
	private void paintExtraBackground(Graphics2D graphics, Container parent,
			Component comp, int width, int height, SubstanceColorScheme scheme) {

		// System.out.println(comp.getClass().getSimpleName() + " -- W: " +
		// width
		// + ", H: " + height);
		// System.out.println(graphics.getTransform());
		// System.out.println(graphics.getClip());

		JRootPane rootPane = SwingUtilities.getRootPane(parent);
		// fix for bug 234 - Window doesn't have a root pane.
		int dx = 0;
		JComponent titlePane = null;
		if (rootPane != null) {
			titlePane = SubstanceCoreUtilities.getTitlePane(rootPane);
			if (titlePane != null) {
				if (comp.isShowing() && titlePane.isShowing()) {
					dx += (comp.getLocationOnScreen().x - titlePane
							.getLocationOnScreen().x);
				} else {
					// have to traverse the hierarchy
					Component c = comp;
					dx = 0;
					while (c != rootPane) {
						dx += c.getX();
						c = c.getParent();
					}
					c = titlePane;
					while (c != rootPane) {
						dx -= c.getX();
						c = c.getParent();
					}
				}
			}
		}
		// int dy = comp.getY();

		int pWidth = (titlePane == null) ? parent.getWidth() : titlePane
				.getWidth();
		// int pHeight = parent.getHeight();
		// System.out.println("DX: " + dx + ", pW: " + pWidth);// + ", pH: " +
		// pHeight);

		if (pWidth != 0) {
			LinearGradientPaint gradientBottom = new LinearGradientPaint(-dx,
					0, -dx + pWidth, 0, new float[] { 0.0f, 0.5f, 1.0f },
					new Color[] { scheme.getMidColor(), scheme.getLightColor(),
							scheme.getMidColor() }, CycleMethod.REPEAT);
			Graphics2D g2d = (Graphics2D) graphics.create();
			g2d.setPaint(gradientBottom);
			g2d.fillRect(-dx, 0, pWidth, height);
			g2d.dispose();
		}

	}
}
