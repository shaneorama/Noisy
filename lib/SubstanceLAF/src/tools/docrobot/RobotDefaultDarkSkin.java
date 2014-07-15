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
package tools.docrobot;

import org.jvnet.substance.api.*;
import org.jvnet.substance.api.painter.overlay.BottomLineOverlayPainter;
import org.jvnet.substance.colorscheme.BlendBiColorScheme;
import org.jvnet.substance.colorscheme.DarkMetallicColorScheme;
import org.jvnet.substance.painter.border.ClassicBorderPainter;
import org.jvnet.substance.painter.decoration.*;
import org.jvnet.substance.painter.gradient.ClassicGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;
import org.jvnet.substance.watermark.SubstancePlanktonWatermark;
import org.jvnet.substance.watermark.SubstanceWatermark;

/**
 * The default dark skin for the docrobot scripts.
 * 
 * @author Kirill Grouchnikov
 */
public class RobotDefaultDarkSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static String NAME = "Robot Default Dark";

	/**
	 * Creates the skin based on the specified color scheme.
	 * 
	 * @param colorScheme
	 *            The active color scheme.
	 */
	public RobotDefaultDarkSkin(SubstanceColorScheme colorScheme) {
		SubstanceColorScheme inactiveScheme = new BlendBiColorScheme(
				colorScheme, new DarkMetallicColorScheme(), 0.6);
		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				colorScheme, inactiveScheme, inactiveScheme);
		defaultSchemeBundle.registerColorScheme(inactiveScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED,
				ComponentState.DISABLED_SELECTED);

		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				DecorationAreaType.NONE);

		this.registerAsDecorationArea(colorScheme,
				DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER);

		this.selectedTabFadeStart = 1.0;
		this.selectedTabFadeEnd = 1.0;

		BottomLineOverlayPainter bottomLineOverlayPainter = new BottomLineOverlayPainter(
				ColorSchemeSingleColorQuery.MID);
		this.addOverlayPainter(bottomLineOverlayPainter,
				DecorationAreaType.PRIMARY_TITLE_PANE,
				DecorationAreaType.SECONDARY_TITLE_PANE,
				DecorationAreaType.HEADER);

		this.watermark = new SubstancePlanktonWatermark();
		this.watermarkScheme = new BlendBiColorScheme(colorScheme,
				new DarkMetallicColorScheme(), 0.5);

		this.buttonShaper = new ClassicButtonShaper();
		this.gradientPainter = new ClassicGradientPainter();
		this.borderPainter = new ClassicBorderPainter();

		MarbleNoiseDecorationPainter decorationPainter = new MarbleNoiseDecorationPainter();
		decorationPainter.setBaseDecorationPainter(new ArcDecorationPainter());
		decorationPainter.setTextureAlpha(0.3f);
		this.decorationPainter = decorationPainter;

		this.highlightPainter = new ClassicHighlightPainter();
		this.borderPainter = new ClassicBorderPainter();
	}

	/**
	 * Creates the skin based on the specified color scheme and watermark.
	 * 
	 * @param colorScheme
	 *            The active color scheme.
	 * @param watermark
	 *            Watermark.
	 */
	public RobotDefaultDarkSkin(SubstanceColorScheme colorScheme,
			SubstanceWatermark watermark) {
		this(colorScheme);
		this.watermark = watermark;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.substance.skin.SubstanceSkin#getDisplayName()
	 */
	public String getDisplayName() {
		return NAME;
	}
}
