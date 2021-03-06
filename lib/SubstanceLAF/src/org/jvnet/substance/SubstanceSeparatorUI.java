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
package org.jvnet.substance;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.painter.utils.SeparatorPainterUtils;
import org.jvnet.substance.utils.SubstanceCoreUtilities;
import org.jvnet.substance.utils.SubstanceSizeUtils;
import org.jvnet.substance.utils.menu.MenuUtilities;
import org.jvnet.substance.utils.menu.SubstanceMenuBackgroundDelegate;

/**
 * UI for separators in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceSeparatorUI extends BasicSeparatorUI {
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceSeparatorUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicSeparatorUI#paint(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g, JComponent c) {

		Component parent = c.getParent();
		if (!(parent instanceof JPopupMenu)) {
			SeparatorPainterUtils.paintSeparator(c, g, c.getWidth(), c
					.getHeight(), ((JSeparator) c).getOrientation());
			return;
		}

		Graphics2D graphics = (Graphics2D) g.create();

		int xOffset = MenuUtilities.getTextOffset(c, parent);
		SubstanceMenuBackgroundDelegate.paintBackground(graphics, c, xOffset);
		Dimension s = c.getSize();
		int startX = 0;
		int width = s.width;
		if (parent.getComponentOrientation().isLeftToRight()) {
			startX = xOffset - 2;
			width = s.width - startX;
		} else {
			startX = 0;
			width = xOffset - 4;
		}
		graphics.translate(startX, 0);
		graphics.setComposite(TransitionLayout.getAlphaComposite(parent));
		SeparatorPainterUtils.paintSeparator(c, graphics, width, s.height,
				((JSeparator) c).getOrientation(), true, 2);

		graphics.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicSeparatorUI#getPreferredSize(javax.swing.
	 * JComponent)
	 */
	@Override
	public Dimension getPreferredSize(JComponent c) {
		float borderStrokeWidth = SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(c));
		int prefSize = (int) (Math.ceil(2.0 * borderStrokeWidth));
		if (((JSeparator) c).getOrientation() == SwingConstants.VERTICAL)
			return new Dimension(prefSize, 0);
		else
			return new Dimension(0, prefSize);
	}
}
