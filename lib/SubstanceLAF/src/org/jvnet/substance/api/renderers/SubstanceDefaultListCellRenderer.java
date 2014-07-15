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
package org.jvnet.substance.api.renderers;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ListUI;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.substance.SubstanceListUI;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.*;
import org.jvnet.substance.utils.*;

/**
 * Default renderer for list cells.
 * 
 * @author Kirill Grouchnikov
 */
@SubstanceApi
@SubstanceRenderer
public class SubstanceDefaultListCellRenderer extends DefaultListCellRenderer {
	/**
	 * Constructs a default renderer object for an item in a list.
	 */
	public SubstanceDefaultListCellRenderer() {
		super();
		this.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing
	 * .JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		this.setComponentOrientation(list.getComponentOrientation());

		ListUI listUI = list.getUI();
		if (listUI instanceof SubstanceListUI) {
			SubstanceListUI ui = (SubstanceListUI) listUI;
			ComponentState state = ui.getCellState(index, this);
			ComponentState prevState = ui.getPrevCellState(index);

			SubstanceColorScheme scheme = (state == ComponentState.DEFAULT) ? ui
					.getDefaultColorScheme()
					: ui.getHighlightColorScheme(state);
			if (scheme == null) {
				scheme = (state == ComponentState.DEFAULT) ? SubstanceColorSchemeUtilities
						.getColorScheme(list, state)
						: SubstanceColorSchemeUtilities.getColorScheme(list,
								ColorSchemeAssociationKind.HIGHLIGHT, state);
			}
			SubstanceColorScheme prevScheme = scheme;
			if (prevState != state) {
				prevScheme = (prevState == ComponentState.DEFAULT) ? ui
						.getDefaultColorScheme() : ui
						.getHighlightColorScheme(prevState);
				if (prevScheme == null) {
					prevScheme = (prevState == ComponentState.DEFAULT) ? SubstanceColorSchemeUtilities
							.getColorScheme(list, prevState)
							: SubstanceColorSchemeUtilities.getColorScheme(
									list, ColorSchemeAssociationKind.HIGHLIGHT,
									prevState);
				}
			}
			Color color = SubstanceColorUtilities
					.getInterpolatedForegroundColor(list, index, scheme, state,
							prevScheme, prevState, FadeKind.SELECTION,
							FadeKind.ROLLOVER);

			// System.out.println("[row " + index + "] - " + prevState.name()
			// + "[" + prevScheme.getDisplayName() + "] -> "
			// + state.name() + "[" + scheme.getDisplayName() + "]\n\t"
			// + color);

			super.setForeground(new ColorUIResource(color));
		} else {
			if (isSelected) {
				this.setForeground(list.getSelectionForeground());
			} else {
				this.setForeground(list.getForeground());
			}
		}

		if (isSelected) {
			// setBackground(list.getSelectionBackground());
			// this.setForeground(list.getSelectionForeground());
		} else {
			if (SubstanceLookAndFeel.isCurrentLookAndFeel())
				SubstanceStripingUtils
						.applyStripedBackground(list, index, this);
			// this.setForeground(list.getForeground());
		}

		if (value instanceof Icon) {
			this.setIcon((Icon) value);
			this.setText("");
		} else {
			this.setIcon(null);
			this.setText((value == null) ? "" : value.toString());
		}

		this.setEnabled(list.isEnabled());
		this.setFont(list.getFont());

		Insets ins = SubstanceSizeUtils
				.getListCellRendererInsets(SubstanceSizeUtils
						.getComponentFontSize(list));
		this
				.setBorder(new EmptyBorder(ins.top, ins.left, ins.bottom,
						ins.right));

		this.setOpaque(false);
		return this;
	}

	/**
	 * UI resource for renderer (does nothing yet).
	 * 
	 * @author Kirill Grouchnikov
	 */
	public static class SubstanceUIResource extends
			SubstanceDefaultListCellRenderer implements
			javax.swing.plaf.UIResource {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing
		 * .JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			return super.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public final void paint(Graphics g) {
		super.paint(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected final void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}
