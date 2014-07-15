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
import java.text.DateFormat;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.TableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.lafwidget.animation.FadeState;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.SubstanceTableUI;
import org.jvnet.substance.SubstanceTableUI.TableCellId;
import org.jvnet.substance.api.*;
import org.jvnet.substance.utils.*;
import org.jvnet.substance.utils.border.SubstanceBorder;

/**
 * Default renderer for table cells.
 * 
 * @author Kirill Grouchnikov
 */
@SubstanceApi
@SubstanceRenderer
public class SubstanceDefaultTableCellRenderer extends DefaultTableCellRenderer {
	/**
	 * Renderer for boolean columns.
	 * 
	 * @author Kirill Grouchnikov
	 */
	@SubstanceRenderer
	public static class BooleanRenderer extends JCheckBox implements
			TableCellRenderer {
		/**
		 * Border for cells that do not have focus.
		 */
		private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

		/**
		 * Creates a new renderer for boolean columns.
		 */
		public BooleanRenderer() {
			super();
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setBorderPainted(true);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.table.TableCellRenderer#getTableCellRendererComponent
		 * (javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (isSelected) {
				this.setForeground(table.getSelectionForeground());
				// super.setBackground(table.getSelectionBackground());
			} else {
				this.setForeground(table.getForeground());
			}
			SubstanceStripingUtils.applyStripedBackground(table, row, this);

			this.setSelected(((value != null) && ((Boolean) value)
					.booleanValue()));
			this.setEnabled(table.isEnabled());

			TableUI tableUI = table.getUI();
			if (tableUI instanceof SubstanceTableUI) {
				SubstanceTableUI ui = (SubstanceTableUI) tableUI;

				// Recompute the focus indication to prevent flicker - JTable
				// registers a listener on selection changes and repaints the
				// relevant cell before our listener (in TableUI) gets the
				// chance to start the fade sequence. The result is that the
				// first frame uses full opacity, and the next frame starts the
				// fade sequence. So, we use the UI delegate to compute the
				// focus indication.
				hasFocus = ui.isFocusedCell(row, column);

				TableCellId cellFocusId = new TableCellId(row, column);
				// set indication to make exact comparison (since
				// focus can be only on one cell).
				cellFocusId.setExactComparison(true);
				FadeState focusState = SubstanceFadeUtilities.getFadeState(
						table, cellFocusId, FadeKind.FOCUS);
				if (hasFocus || (focusState != null)) {
					SubstanceBorder border = new SubstanceBorder();
					if (focusState != null) {
						border.setAlpha(focusState.getFadePosition());
					}
					this.setBorder(border);
				} else {
					this.setBorder(BooleanRenderer.noFocusBorder);
				}
			} else {
				if (hasFocus) {
					this.setBorder(UIManager
							.getBorder("Table.focusCellHighlightBorder"));
				} else {
					this.setBorder(BooleanRenderer.noFocusBorder);
				}
			}

			this.setOpaque(false);

			return this;
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

		@Override
		protected final void paintBorder(Graphics g) {
		}

		@Override
		public final void paintComponents(Graphics g) {
		}
	}

	/**
	 * Renderer for icon columns.
	 * 
	 * @author Kirill Grouchnikov
	 */
	public static class IconRenderer extends SubstanceDefaultTableCellRenderer {
		/**
		 * Creates a new renderer for icon columns.
		 */
		public IconRenderer() {
			super();
			this.setHorizontalAlignment(SwingConstants.CENTER);
		}

		@Override
		public void setValue(Object value) {
			this.setIcon((value instanceof Icon) ? (Icon) value : null);
			this.setText(null);
		}
	}

	/**
	 * Renderer for number columns.
	 * 
	 * @author Kirill Grouchnikov
	 */
	public static class NumberRenderer extends
			SubstanceDefaultTableCellRenderer {
		/**
		 * Creates a new renderer for number columns.
		 */
		public NumberRenderer() {
			super();
			this.setHorizontalAlignment(SwingConstants.RIGHT);
		}
	}

	/**
	 * Renderer for double columns.
	 * 
	 * @author Kirill Grouchnikov
	 */
	public static class DoubleRenderer extends NumberRenderer {
		/**
		 * Number formatter for this renderer.
		 */
		NumberFormat formatter;

		/**
		 * Creates a new renderer for double columns.
		 */
		public DoubleRenderer() {
			super();
		}

		@Override
		public void setValue(Object value) {
			if (this.formatter == null) {
				this.formatter = NumberFormat.getInstance();
			}
			this.setText((value == null) ? "" : this.formatter.format(value));
		}
	}

	/**
	 * Renderer for date columns.
	 * 
	 * @author Kirill Grouchnikov
	 */
	public static class DateRenderer extends SubstanceDefaultTableCellRenderer {
		/**
		 * Date formatter for this renderer.
		 */
		DateFormat formatter;

		/**
		 * Creates a new renderer for date columns.
		 */
		public DateRenderer() {
			super();
		}

		@Override
		public void setValue(Object value) {
			if (this.formatter == null) {
				this.formatter = DateFormat.getDateInstance();
			}
			this.setText((value == null) ? "" : this.formatter.format(value));
		}
	}

	/**
	 * Creates a default opaque table cell renderer.
	 */
	public SubstanceDefaultTableCellRenderer() {
		this.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax
	 * .swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (!SubstanceLookAndFeel.isCurrentLookAndFeel())
			return super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);

		TableUI tableUI = table.getUI();
		SubstanceTableUI ui = (SubstanceTableUI) tableUI;

		// Recompute the focus indication to prevent flicker - JTable
		// registers a listener on selection changes and repaints the
		// relevant cell before our listener (in TableUI) gets the
		// chance to start the fade sequence. The result is that the
		// first frame uses full opacity, and the next frame starts the
		// fade sequence. So, we use the UI delegate to compute the
		// focus indication.
		hasFocus = ui.isFocusedCell(row, column);

		TableCellId cellId = new TableCellId(row, column);
		Comparable<?> compId = ui.getId(row, column);
		ComponentState state = ui.getCellState(cellId);
		ComponentState prevState = ui.getPrevCellState(cellId);

		SubstanceColorScheme scheme = (state == ComponentState.DEFAULT) ? ui
				.getDefaultColorScheme() : ui.getHighlightColorScheme(state);
		if (scheme == null) {
			scheme = (state == ComponentState.DEFAULT) ? SubstanceColorSchemeUtilities
					.getColorScheme(table, state)
					: SubstanceColorSchemeUtilities.getColorScheme(table,
							ColorSchemeAssociationKind.HIGHLIGHT, state);
		}
		SubstanceColorScheme prevScheme = scheme;
		if (prevState != state) {
			prevScheme = (prevState == ComponentState.DEFAULT) ? ui
					.getDefaultColorScheme() : ui
					.getHighlightColorScheme(prevState);
			if (prevScheme == null) {
				prevScheme = (prevState == ComponentState.DEFAULT) ? SubstanceColorSchemeUtilities
						.getColorScheme(table, prevState)
						: SubstanceColorSchemeUtilities
								.getColorScheme(table,
										ColorSchemeAssociationKind.HIGHLIGHT,
										prevState);
			}
		}

		// SubstanceColorScheme scheme = (state == ComponentState.DEFAULT) ?
		// ui
		// .getDefaultColorScheme() : SubstanceColorSchemeUtilities
		// .getColorScheme(table, state);
		if (scheme == null)
			scheme = SubstanceColorSchemeUtilities.getColorScheme(table, state);

		if (ui.hasRolloverAnimations() || ui.hasSelectionAnimations()) {
			super.setForeground(new ColorUIResource(SubstanceColorUtilities
					.getInterpolatedForegroundColor(table, compId, scheme,
							state, prevScheme, prevState, FadeKind.SELECTION,
							FadeKind.ROLLOVER)));
		} else {
			super
					.setForeground(new ColorUIResource(scheme
							.getForegroundColor()));
		}

		SubstanceStripingUtils.applyStripedBackground(table, row, this);

		this.setFont(table.getFont());

		TableCellId cellFocusId = new TableCellId(row, column);
		// set indication to make exact comparison (since
		// focus can be only on one cell).
		cellFocusId.setExactComparison(true);
		FadeState focusState = SubstanceFadeUtilities.getFadeState(table,
				cellFocusId, FadeKind.FOCUS);

		Insets regInsets = ui.getCellRendererInsets();
		if (hasFocus || (focusState != null)) {
			SubstanceBorder border = new SubstanceBorder(regInsets);

			// System.out.println("[" + row + ":" + column + "] hasFocus : "
			// + hasFocus + ", focusState : " + focusState);
			if (focusState != null) {
				border.setAlpha(focusState.getFadePosition());
			}

			// special case for tables with no grids
			if (!table.getShowHorizontalLines()
					&& !table.getShowVerticalLines()) {
				this.setBorder(new CompoundBorder(new EmptyBorder(table
						.getRowMargin() / 2, 0, table.getRowMargin() / 2, 0),
						border));
			} else {
				this.setBorder(border);
			}
		} else {
			this.setBorder(new EmptyBorder(regInsets.top, regInsets.left,
					regInsets.bottom, regInsets.right));
		}

		this.setValue(value);
		this.setOpaque(false);
		this.setEnabled(table.isEnabled());
		return this;
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

	@Override
	protected final void paintBorder(Graphics g) {
	}

	@Override
	public final void paintComponents(Graphics g) {
	}
}
