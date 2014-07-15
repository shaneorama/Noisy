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

import org.jvnet.substance.api.SubstanceColorSchemeBundle;
import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.colorscheme.*;
import org.jvnet.substance.painter.border.StandardBorderPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.decoration.Glass3DDecorationPainter;
import org.jvnet.substance.painter.gradient.StandardGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.StandardButtonShaper;
import org.jvnet.substance.watermark.SubstanceStripeWatermark;

/**
 * The legacy default look and feel that provides the pre-5.0 default Substance
 * skin.
 * 
 * @author Kirill Grouchnikov
 * @since 5.0
 */
public class SubstanceLegacyDefaultLookAndFeel extends SubstanceLookAndFeel {
	/**
	 * Creates the legacy default look-and-feel.
	 */
	public SubstanceLegacyDefaultLookAndFeel() {
		super(new SubstanceLegacyDefaultSkin());
	}

	/**
	 * Pre-5.0 default skin.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private static class SubstanceLegacyDefaultSkin extends SubstanceSkin {
		/**
		 * Creates the default skin.
		 */
		public SubstanceLegacyDefaultSkin() {
			SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
					new AquaColorScheme(), new MetallicColorScheme(),
					new LightGrayColorScheme());
			this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
					DecorationAreaType.NONE);

			this.watermark = new SubstanceStripeWatermark();
			this.buttonShaper = new StandardButtonShaper();
			this.gradientPainter = new StandardGradientPainter();
			this.borderPainter = new StandardBorderPainter();
			this.highlightPainter = new ClassicHighlightPainter();
			this.decorationPainter = new Glass3DDecorationPainter();
		}

		public String getDisplayName() {
			return "Legacy Default";
		}
	}
}
