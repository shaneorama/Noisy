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
package org.jvnet.substance.shaper;

import java.awt.Insets;
import java.awt.Shape;

import javax.swing.AbstractButton;

/**
 * Base button shaper. This class is <b>for internal use only</b>.
 * 
 * @author Kirill Grouchnikov
 */
public abstract class BaseButtonShaper implements SubstanceButtonShaper {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.shaper.SubstanceButtonShaper#getButtonOutline(javax
	 * .swing.JComponent, java.awt.Insets)
	 */
	@Override
	public final Shape getButtonOutline(AbstractButton button, Insets insets) {
		return this.getButtonOutline(button, insets, button.getWidth(), button
				.getHeight(), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.shaper.SubstanceButtonShaper#getButtonOutline(javax
	 * .swing.JComponent)
	 */
	@Override
	public final Shape getButtonOutline(AbstractButton button) {
		return this.getButtonOutline(button, null, button.getWidth(), button
				.getHeight(), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.shaper.SubstanceButtonShaper#getButtonOutline(javax
	 * .swing.AbstractButton, java.awt.Insets, int, int)
	 */
	@Override
	public final Shape getButtonOutline(AbstractButton button, Insets insets,
			int width, int height) {
		return this.getButtonOutline(button, null, width, height, false);
	}
}
