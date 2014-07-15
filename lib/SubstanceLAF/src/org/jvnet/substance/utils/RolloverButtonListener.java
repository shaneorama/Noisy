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
package org.jvnet.substance.utils;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.event.*;
import java.security.AccessControlException;

import javax.swing.AbstractButton;
import javax.swing.plaf.basic.BasicButtonListener;

/**
 * Button listener for rollover effects. Tracks the mouse motion and focus
 * interaction for the associated button. This class is <b>for internal use
 * only</b>.
 * 
 * @author Kirill Grouchnikov
 */
public class RolloverButtonListener extends BasicButtonListener {
	/**
	 * If the mouse pointer is currently inside the associated button area,
	 * <code>this</code> flag is <code>true</code>.
	 */
	private boolean isMouseInside;

	/**
	 * The associated button.
	 */
	private AbstractButton button;

	/**
	 * Simple constructor.
	 * 
	 * @param b
	 *            The associated button.
	 */
	public RolloverButtonListener(AbstractButton b) {
		super(b);
		this.button = b;
		this.isMouseInside = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		this.isMouseInside = true;
		boolean isMouseDrag = ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0);
		// System.err.println("ENTERED : " + e.getModifiers() + ":"
		// + e.getModifiersEx());
		if (!isMouseDrag) {
			this.button.getModel().setRollover(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		// AbstractButton button = (AbstractButton) e.getSource();
		this.isMouseInside = false;
		this.button.getModel().setRollover(false);
		// FadeTracker.getInstance().trackFadeOut(FadeKind.ROLLOVER, button,
		// false, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		for (ActionListener al : this.button.getActionListeners())
			if (al instanceof SubstanceInternalFrameTitlePane.ClickListener)
				return;
		this.button.getModel().setRollover(this.isMouseInside);
		// button.getModel().setRollover(this.isMouseInside);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicButtonListener#mouseMoved(java.awt.event.
	 * MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		for (ActionListener al : this.button.getActionListeners())
			if (al instanceof SubstanceInternalFrameTitlePane.ClickListener)
				return;
		this.button.getModel().setRollover(this.isMouseInside);
	}

	//
	// @Override
	// public void mouseDragged(MouseEvent e) {
	// super.mouseDragged(e);
	// System.err.println("DRAGGED : " + e.getModifiers());
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicButtonListener#focusGained(java.awt.event
	 * .FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		super.focusGained(e);
		if (!this.button.isShowing()) {
			// shouldn't happen. Is some lurking Swing bug
			return;
		}
		try {
			PointerInfo pi = MouseInfo.getPointerInfo();
			int px = pi.getLocation().x - this.button.getLocationOnScreen().x;
			int py = pi.getLocation().y - this.button.getLocationOnScreen().y;
			this.button.getModel().setRollover(this.button.contains(px, py));
		} catch (AccessControlException ace) {
			// sandbox - give up
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.swing.plaf.basic.BasicButtonListener#focusLost(java.awt.event.
	 * FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {
		super.focusLost(e);
		this.button.getModel().setRollover(false);
		// FadeTracker.getInstance().trackFadeOut(button);
	}
}
