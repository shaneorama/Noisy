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
import javax.swing.plaf.ListUI;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.substance.SubstanceListUI;
import org.jvnet.substance.api.*;
import org.jvnet.substance.utils.*;

/**
 * Renderer for combo boxes.
 * 
 * @author Kirill Grouchnikov
 */
@SubstanceApi
@SubstanceRenderer
public class SubstanceDefaultComboBoxRenderer extends
		SubstanceDefaultListCellRenderer {
	/**
	 * The associated combo box.
	 */
	private JComboBox combo;

	/**
	 * Simple constructor.
	 * 
	 * @param combo
	 *            The associated combo box.
	 */
	public SubstanceDefaultComboBoxRenderer(JComboBox combo) {
		super();
		this.combo = combo;
		// this.setOpaque(true);

		Insets ins = SubstanceSizeUtils
				.getListCellRendererInsets(SubstanceSizeUtils
						.getComponentFontSize(combo));
		this
				.setBorder(new EmptyBorder(ins.top, ins.left, ins.bottom,
						ins.right));
		//
		// Insets i = b.getBorderInsets(combo);
		// System.out.println("Combo inner - " + combo.getFont().getSize() +" :
		// "
		// + i.top + ", " + i.left + ", " + i.bottom + ", " + i.right);
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

		JComponent result = (JComponent) super.getListCellRendererComponent(
				list, value, index, isSelected, cellHasFocus);

		ListUI listUI = list.getUI();
		if (listUI instanceof SubstanceListUI) {
			SubstanceListUI ui = (SubstanceListUI) listUI;
			ComponentState state = ui.getCellState(index, result);
			ComponentState prevState = ui.getPrevCellState(index);

			// special case for the combobox. The selected value is
			// painted using the renderer of the list, and the index
			// is -1.
			if (index == -1) {
				boolean isEnabled = this.combo.isEnabled();
				if (isSelected && isEnabled) {
					state = ComponentState.SELECTED;
					prevState = ComponentState.SELECTED;
				}
				if (!isEnabled) {
					state = ComponentState.DISABLED_UNSELECTED;
					prevState = ComponentState.DISABLED_UNSELECTED;
				}
			}
			Component compForSchemeQuery = (index == -1) ? combo : list;
			// use highlight color scheme for selected and focused
			// elements
			SubstanceColorScheme scheme = isSelected ? SubstanceColorSchemeUtilities
					.getColorScheme(compForSchemeQuery,
							ColorSchemeAssociationKind.HIGHLIGHT, state)
					: SubstanceColorSchemeUtilities.getColorScheme(
							compForSchemeQuery, state);
			Color color = SubstanceColorUtilities
					.getInterpolatedForegroundColor(compForSchemeQuery, index,
							scheme, state, scheme, prevState, FadeKind.SELECTION,
							FadeKind.ROLLOVER);

			result.setForeground(color);

			SubstanceStripingUtils.applyStripedBackground(combo, index, this);
		}
		result.setOpaque(!isSelected && (index >= 0));
		result.setEnabled(combo.isEnabled());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension size;

		if ((this.getText() == null) || (this.getText().equals(""))) {
			this.setText(" ");
			size = super.getPreferredSize();
			this.setText("");
		} else {
			size = super.getPreferredSize();
		}

		return size;
	}

	/**
	 * UI resource for renderer (does nothing yet).
	 * 
	 * @author Kirill Grouchnikov
	 */
	public static class SubstanceUIResource extends
			SubstanceDefaultComboBoxRenderer implements
			javax.swing.plaf.UIResource {
		/**
		 * Creates a new renderer resource.
		 * 
		 * @param combo
		 *            Combobox.
		 */
		public SubstanceUIResource(JComboBox combo) {
			super(combo);
		}
	}
}
