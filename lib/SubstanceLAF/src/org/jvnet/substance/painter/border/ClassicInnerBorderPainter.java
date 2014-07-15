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

import org.jvnet.substance.api.SubstanceConstants.ColorShiftKind;

/**
 * Implementation of border painter that uses the {@link ClassicBorderPainter}
 * gradients and paints the inner contour of the border in addition to the outer
 * contour.
 * 
 * @author Kirill Grouchnikov
 * @deprecated Will be removed in version 6.0. Use
 *             {@link CompositeBorderPainter} and {@link DelegateBorderPainter}
 *             instead.
 */
@Deprecated
public class ClassicInnerBorderPainter extends InnerDelegateBorderPainter {
	/**
	 * Display name.
	 */
	private static final String NAME = "Classic Inner";

	/**
	 * Creates a new inner border painter based on a
	 * {@link ClassicBorderPainter
	 * }.
	 */
	public ClassicInnerBorderPainter() {
		super(NAME, new ClassicBorderPainter());
	}

	/**
	 * Creates a new inner border painter based on a
	 * {@link ClassicBorderPainter
	 * }.
	 * 
	 * @param shiftCoef
	 *            Shift coefficient. Must be in 0.0-1.0 range.
	 * @param shiftKind
	 *            Shift kind.
	 */
	public ClassicInnerBorderPainter(float shiftCoef, ColorShiftKind shiftKind) {
		super(NAME + " " + shiftCoef + " " + shiftKind.name(),
				new ClassicBorderPainter(), shiftCoef, shiftKind);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.substance.border.InnerDelegateBorderPainter#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return NAME;
	}
}