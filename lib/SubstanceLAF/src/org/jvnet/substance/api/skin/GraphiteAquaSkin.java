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
package org.jvnet.substance.api.skin;

import org.jvnet.substance.api.*;
import org.jvnet.substance.api.painter.fill.FractionBasedFillPainter;
import org.jvnet.substance.api.painter.highlight.FractionBasedHighlightPainter;
import org.jvnet.substance.painter.border.*;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.decoration.FlatDecorationPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;

/**
 * <code>Graphite Aqua</code> skin. This class is part of officially supported
 * API.
 * 
 * @author Kirill Grouchnikov
 * @since version 5.3
 */
public class GraphiteAquaSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "Graphite Aqua";

	/**
	 * Creates a new <code>Raven Graphite</code> skin.
	 */
	public GraphiteAquaSkin() {
		SubstanceSkin.ColorSchemes schemes = SubstanceSkin
				.getColorSchemes(GraphiteAquaSkin.class
						.getClassLoader()
						.getResource(
								"org/jvnet/substance/skin/ravengraphite.colorschemes"));

		SubstanceColorScheme selectedDisabledScheme = schemes
				.get("Raven Graphite Selected Disabled");
		SubstanceColorScheme disabledScheme = schemes
				.get("Raven Graphite Disabled");

		SubstanceColorScheme defaultScheme = schemes
				.get("Raven Graphite Default");
		SubstanceColorScheme backgroundScheme = schemes
				.get("Raven Graphite Background");

		// use the same color scheme for active and default controls
		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				defaultScheme, defaultScheme, disabledScheme);

		// highlight fill scheme + custom alpha for rollover unselected state
		SubstanceColorScheme highlightScheme = schemes
				.get("Raven Graphite Aqua");
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme,
				0.75f, ComponentState.ROLLOVER_UNSELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 0.9f,
				ComponentState.SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 1.0f,
				ComponentState.ROLLOVER_SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 1.0f,
				ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);

		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ColorSchemeAssociationKind.BORDER,
				ComponentState.ROLLOVER_ARMED,
				ComponentState.ROLLOVER_SELECTED,
				ComponentState.ROLLOVER_UNSELECTED);
		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ColorSchemeAssociationKind.FILL, ComponentState.SELECTED,
				ComponentState.ROLLOVER_SELECTED);

		// border schemes
		SubstanceColorScheme borderScheme = schemes
				.get("Raven Graphite Border");
		SubstanceColorScheme separatorScheme = schemes
				.get("Raven Graphite Separator");
		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ColorSchemeAssociationKind.HIGHLIGHT_BORDER, ComponentState
						.getActiveStates());
		defaultSchemeBundle.registerColorScheme(borderScheme,
				ColorSchemeAssociationKind.BORDER);
		defaultSchemeBundle.registerColorScheme(separatorScheme,
				ColorSchemeAssociationKind.SEPARATOR);
		defaultSchemeBundle.registerColorScheme(borderScheme,
				ColorSchemeAssociationKind.MARK);

		// text highlight scheme
		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ColorSchemeAssociationKind.TEXT_HIGHLIGHT,
				ComponentState.SELECTED, ComponentState.ROLLOVER_SELECTED);

		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);

		defaultSchemeBundle.registerColorScheme(disabledScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED);
		defaultSchemeBundle.registerColorScheme(selectedDisabledScheme, 0.5f,
				ComponentState.DISABLED_SELECTED);
		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				backgroundScheme, DecorationAreaType.NONE);

		this.setSelectedTabFadeStart(0.15);
		this.setSelectedTabFadeEnd(0.25);

		this.buttonShaper = new ClassicButtonShaper();
		this.watermark = null;
		this.gradientPainter = new FractionBasedFillPainter("Graphite Aqua",
				new float[] { 0.0f, 0.5f, 1.0f },
				new ColorSchemeSingleColorQuery[] {
						ColorSchemeSingleColorQuery.LIGHT,
						ColorSchemeSingleColorQuery.MID,
						ColorSchemeSingleColorQuery.MID });

		this.decorationPainter = new FlatDecorationPainter();
		this.highlightPainter = new FractionBasedHighlightPainter(
				"Graphite Aqua", new float[] { 0.0f, 0.5f, 1.0f },
				new ColorSchemeSingleColorQuery[] {
						ColorSchemeSingleColorQuery.EXTRALIGHT,
						ColorSchemeSingleColorQuery.LIGHT,
						ColorSchemeSingleColorQuery.MID });
		this.borderPainter = new CompositeBorderPainter("Graphite Aqua",
				new ClassicBorderPainter(), new DelegateBorderPainter(
						"Graphite Aqua Inner", new ClassicBorderPainter(),
						0xC0FFFFFF, 0x90FFFFFF, 0x30FFFFFF,
						new ColorSchemeTransform() {
							@Override
							public SubstanceColorScheme transform(
									SubstanceColorScheme scheme) {
								return scheme.tint(0.25f);
							}
						}));
		this.highlightBorderPainter = new ClassicBorderPainter();
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
