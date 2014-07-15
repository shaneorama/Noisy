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

import org.jvnet.substance.api.SubstanceColorScheme;

/**
 * Empty implementation of {@link SubstanceBorderPainter} which doesn't paint
 * any border. This is useful for migration of custom gradient painters which,
 * until version 4.0, were used to paint both the control background and the
 * control border. Starting from version 4.0, the borders are painted by the
 * currently installed border painter, and as such, a custom gradient painter
 * that paints a border will be overriden by the current border painter. To
 * allow easier migration (splitting the old custom gradient painter into a
 * custom gradient painter <b>and</b> a custom border painter), you can use this
 * implementation that will not override the custom border.
 * 
 * @author Kirill Grouchnikov
 * @deprecated Will be removed in version 6.0
 */
@Deprecated
public class NullBorderPainter implements SubstanceBorderPainter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.border.SubstanceBorderPainter#getDisplayName()
	 */
	public String getDisplayName() {
		return "Null";
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
	}

	@Override
	public boolean isPaintingInnerContour() {
		return false;
	}
}
