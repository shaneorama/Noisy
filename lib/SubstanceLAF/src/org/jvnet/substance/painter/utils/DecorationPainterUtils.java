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
import org.jvnet.substance.painter.decoration.SubstanceDecorationPainter;
import org.jvnet.substance.utils.SubstanceCoreUtilities;
import org.jvnet.substance.watermark.SubstanceWatermark;

/**
 * Contains utility methods related to decoration painters. This class is for
 * internal use only.
 * 
 * @author Kirill Grouchnikov
 */
public class DecorationPainterUtils {
	/**
	 * Client property for marking a component with an instance of
	 * {@link DecorationAreaType} enum.
	 */
	private static final String DECORATION_AREA_TYPE = "substancelaf.internal.painter.decorationAreaType";

	/**
	 * Client property for specifying whether the value of
	 * {@link #DECORATION_AREA_TYPE} client property should propagate to the
	 * component children.
	 */
	// private static final String DECORATION_AREA_PROPAGATES =
	// "substancelaf.internal.painter.decorationAreaPropagates";
	/**
	 * Sets the decoration type of the specified component.
	 * 
	 * @param comp
	 *            Component.
	 * @param type
	 *            Decoration type of the component and its children if
	 *            <code>isPropagatingToChildren</code> is <code>true</code>.
	 * @param isPropagatingToChildren
	 *            If <code>true</code>, the decoration type will be applied to
	 *            the component children.
	 */
	public static void setDecorationType(JComponent comp,
			DecorationAreaType type) {// , boolean isPropagatingToChildren) {
		comp.putClientProperty(DECORATION_AREA_TYPE, type);
		// if (!isPropagatingToChildren) {
		// comp.putClientProperty(DECORATION_AREA_PROPAGATES, Boolean.FALSE);
		// }
	}

	/**
	 * Clears the client properties related to the decoration area type.
	 * 
	 * @param comp
	 *            Component.
	 */
	public static void clearDecorationType(JComponent comp) {
		if (comp != null) {
			comp.putClientProperty(DECORATION_AREA_TYPE, null);
			// comp.putClientProperty(DECORATION_AREA_PROPAGATES, null);
		}
	}

	/**
	 * Returns the decoration area type of the specified component. The
	 * component and its ancestor hierarchy are scanned for the registered
	 * decoration area type. The farthest ancestor that was passed to
	 * {@link #setDecorationType(JComponent, DecorationAreaType, boolean)} with
	 * the <code>isPropagatingToChildren</code> not set to <code>false</code>
	 * (the second part is not checked for the component itself) defines the
	 * result.
	 * 
	 * @param comp
	 *            Component.
	 * @return Decoration area type of the component.
	 */
	public static DecorationAreaType getDecorationType(Component comp) {
		Component c = comp;
		DecorationAreaType result = null;
		while (c != null) {
			if (c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				Object prop = jc.getClientProperty(DECORATION_AREA_TYPE);
				if (prop instanceof DecorationAreaType) {
					if (prop == DecorationAreaType.NONE)
						return null;
					return (DecorationAreaType) prop;
				}
			}
			c = c.getParent();
		}
		return result;
	}

	/**
	 * Returns the immediate decoration area type of the specified component.
	 * The component is checked for the registered decoration area type. If
	 * {@link #setDecorationType(JComponent, DecorationAreaType, boolean)} was
	 * not called on this component, this method returns <code>null</code>.
	 * 
	 * @param comp
	 *            Component.
	 * @return Immediate decoration area type of the component.
	 */
	public static DecorationAreaType getImmediateDecorationType(Component comp) {
		Component c = comp;
		if (c instanceof JComponent) {
			JComponent jc = (JComponent) c;
			Object prop = jc.getClientProperty(DECORATION_AREA_TYPE);
			if (prop instanceof DecorationAreaType)
				return (DecorationAreaType) prop;
		}
		return null;
	}

	/**
	 * Paints the decoration background on the specified component. The
	 * decoration background is not painted when the <code>force</code>
	 * parameter is <code>false</code> and at least one of the following
	 * conditions holds:
	 * <ul>
	 * <li>The component is in a cell renderer.</li>
	 * <li>The component is not showing on the screen.</li>
	 * <li>The component is in the preview mode.</li>
	 * </ul>
	 * 
	 * @param g
	 *            Graphics context.
	 * @param c
	 *            Component.
	 * @param force
	 *            If <code>true</code>, the painting of decoration background is
	 *            enforced.
	 */
	public static void paintDecorationBackground(Graphics g, Component c,
			boolean force) {
		DecorationAreaType decorationType = SubstanceLookAndFeel
				.getDecorationType(c);
		paintDecorationBackground(g, c, decorationType, force);
	}

	/**
	 * Paints the decoration background on the specified component. See comments
	 * on {@link #paintDecorationBackground(Graphics, Component, boolean)} for
	 * the cases when the decoration background painting is skipped.
	 * 
	 * @param g
	 *            Graphics context.
	 * @param c
	 *            Component.
	 * @param decorationType
	 *            Decoration area type of the component.
	 * @param force
	 *            If <code>true</code>, the painting of decoration background is
	 *            enforced. #see
	 *            {@link #paintDecorationBackground(Graphics, Component, boolean)}
	 */
	private static void paintDecorationBackground(Graphics g, Component c,
			DecorationAreaType decorationType, boolean force) {
		// System.out.println("Painting " + c.getClass().getSimpleName());
		boolean isInCellRenderer = (c.getParent() instanceof CellRendererPane);
		boolean isPreviewMode = false;
		if (c instanceof JComponent) {
			isPreviewMode = (Boolean.TRUE.equals(((JComponent) c)
					.getClientProperty(LafWidgetUtilities.PREVIEW_MODE)));
		}

		if (!force && !isPreviewMode && !c.isShowing() && !isInCellRenderer) {
			return;
		}

		if ((c.getHeight() == 0) || (c.getWidth() == 0))
			return;

		SubstanceSkin skin = SubstanceCoreUtilities.getSkin(c);
		SubstanceDecorationPainter painter = skin.getDecorationPainter();

		Graphics2D g2d = (Graphics2D) g.create();
		painter.paintDecorationArea(g2d, c, decorationType, c.getWidth(), c
				.getHeight(), skin);

		SubstanceWatermark watermark = SubstanceCoreUtilities.getSkin(c)
				.getWatermark();
		if ((watermark != null) && !isPreviewMode && !isInCellRenderer
				&& c.isShowing() && SubstanceCoreUtilities.toDrawWatermark(c)) {
			// paint the watermark over the component
			watermark.drawWatermarkImage(g2d, c, 0, 0, c.getWidth(), c
					.getHeight());

			// paint the background second time with 50%
			// translucency, making the watermark' bleed' through.
			g2d.setComposite(TransitionLayout.getAlphaComposite(c, 0.5f, g));
			painter.paintDecorationArea(g2d, c, decorationType, c.getWidth(), c
					.getHeight(), skin);
		}
		g2d.dispose();
	}
}
