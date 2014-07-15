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

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

import org.jvnet.substance.utils.SubstanceCoreUtilities;
import org.jvnet.substance.utils.menu.MenuUtilities;

/**
 * UI for popup menus in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstancePopupMenuUI extends BasicPopupMenuUI {
	/**
	 * Tracks changes to the popup menu and invalidates precomputed text offset.
	 */
	protected ContainerListener substanceContainerListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstancePopupMenuUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicPopupMenuUI#installListeners()
	 */
	@Override
	protected void installListeners() {
		super.installListeners();

		this.substanceContainerListener = new ContainerListener() {
			public void componentAdded(ContainerEvent e) {
				MenuUtilities.cleanPopupLayoutMetrics(popupMenu);
			}

			public void componentRemoved(ContainerEvent e) {
				MenuUtilities.cleanPopupLayoutMetrics(popupMenu);
			}
		};
		this.popupMenu.addContainerListener(this.substanceContainerListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicPopupMenuUI#uninstallListeners()
	 */
	@Override
	protected void uninstallListeners() {
		this.popupMenu.removeContainerListener(this.substanceContainerListener);
		this.substanceContainerListener = null;

		super.uninstallListeners();
	}
	//
	// @Override
	// public void update(Graphics g, JComponent c) {
	// MenuLayoutMetrics popupMetrics = MenuUtilities.getPopupLayoutMetrics(
	// (JPopupMenu) c, true);
	//
	// int xOffset = 0;
	// int gap = popupMetrics.maxIconTextGap;
	// Insets i = c.getInsets();
	// Component firstChild = c.getComponent(0);
	// Insets i1 = (firstChild instanceof JComponent) ? ((JComponent)
	// firstChild)
	// .getInsets()
	// : null;
	// if (c.getComponentOrientation().isLeftToRight()) {
	// xOffset = i.left;
	// if (i1 != null)
	// xOffset += i1.left;
	// xOffset += gap / 2;
	// if (popupMetrics.maxCheckIconWidth > 0) {
	// xOffset += (popupMetrics.maxCheckIconWidth + gap);
	// }
	// if (popupMetrics.maxIconWidth > 0) {
	// xOffset += (popupMetrics.maxIconWidth + gap);
	// }
	// xOffset += gap / 2;
	// } else {
	// xOffset = c.getWidth() - i.right;
	// if (i1 != null)
	// xOffset -= i1.right;
	// xOffset -= gap / 2;
	// if (popupMetrics.maxCheckIconWidth > 0) {
	// xOffset -= (popupMetrics.maxCheckIconWidth + gap);
	// }
	// if (popupMetrics.maxIconWidth > 0) {
	// xOffset -= (popupMetrics.maxIconWidth + gap);
	// }
	// xOffset += gap / 2;
	// }
	//
	// SubstanceMenuBackgroundDelegate.paintBackground(g, c, xOffset);
	// }
}
