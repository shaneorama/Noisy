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

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.*;

import org.jvnet.lafwidget.LafWidgetUtilities;
import org.jvnet.lafwidget.animation.*;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.api.*;
import org.jvnet.substance.api.SubstanceConstants.Side;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableHeaderCellRenderer;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.utils.BackgroundPaintingUtils;
import org.jvnet.substance.painter.utils.HighlightPainterUtils;
import org.jvnet.substance.utils.*;

/**
 * UI for tables in <b>Substance</b> look and feel. Unfortunately, the entire
 * painting stack has been copied from {@link BasicTableUI} since the methods
 * are private. The animation effects are implemented in the
 * {@link #paintCell(Graphics, Rectangle, int, int)}.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceTableUI extends BasicTableUI {
	/**
	 * Holds the list of currently selected row-column indexes.
	 */
	protected Map<TableCellId, Object> selectedIndices;

	/**
	 * Holds the currently rolled-over row-column index, or <code>null</code> if
	 * none such.
	 */
	protected TableId rolledOverId;

	/**
	 * Row index of the focused cell.
	 */
	protected int focusedRow;

	/**
	 * Column index of the focused cell.
	 */
	protected int focusedColumn;

	/**
	 * Holds the currently rolled-over column index, or <code>-1</code> if none
	 * such. This is used for the table header animations.
	 */
	protected int rolledOverColumn;

	/**
	 * Map of default renderers.
	 */
	protected Map<Class<?>, TableCellRenderer> defaultRenderers;

	/**
	 * Listener that listens to changes on table properties.
	 */
	protected PropertyChangeListener substancePropertyChangeListener;

	/**
	 * Listener for fade animations on list selections.
	 */
	protected TableStateListener substanceFadeSelectionListener;

	/**
	 * Listener for fade animations on table rollovers.
	 */
	protected RolloverFadeListener substanceFadeRolloverListener;

	/**
	 * Map of previous fade states (for state-aware color scheme transitions).
	 */
	private Map<TableCellId, ComponentState> prevStateMap;

	/**
	 * Map of next fade states (for state-aware color scheme transitions).
	 */
	private Map<TableCellId, ComponentState> nextStateMap;

	/**
	 * Cell renderer insets. Is computed in {@link #installDefaults()} and
	 * reused in
	 * {@link SubstanceDefaultTableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)}
	 * and
	 * {@link SubstanceDefaultTableHeaderCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)}
	 * for performance optimizations.
	 */
	private Insets cellRendererInsets;

	// public static final String IGNORE_COLORS =
	// "substancelaf.internal.table.ignoreColors";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceTableUI();
	}

	/**
	 * Creates a UI delegate for table.
	 */
	public SubstanceTableUI() {
		super();
		this.selectedIndices = new HashMap<TableCellId, Object>();
		this.prevStateMap = new HashMap<TableCellId, ComponentState>();
		this.nextStateMap = new HashMap<TableCellId, ComponentState>();
		this.rolledOverColumn = -1;
		this.focusedRow = -1;
		this.focusedColumn = -1;

		this.rowId = new TableRowId(-1);
		this.colId = new TableColumnId(-1);
		this.cellId = new TableCellId(-1, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTableUI#installDefaults()
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();
		if (SubstanceCoreUtilities.toDrawWatermark(this.table))
			this.table.setOpaque(false);

		// fix for defect 117 - need to restore default table cell
		// renderers when Substance is unset
		this.defaultRenderers = new HashMap<Class<?>, TableCellRenderer>();

		Class<?>[] defClasses = new Class[] { Object.class, Icon.class,
				ImageIcon.class, Number.class, Float.class, Double.class,
				Date.class, Boolean.class };
		for (Class<?> clazz : defClasses) {
			this.defaultRenderers.put(clazz, this.table
					.getDefaultRenderer(clazz));
		}

		// Override default renderers - note fix for issue 194
		// that doesn't override user-specific renderers (those that don't come
		// from JTable class).
		this.installRendererIfNecessary(Object.class,
				new SubstanceDefaultTableCellRenderer());
		// this.table.setDefaultRenderer(Object.class,
		// new SubstanceDefaultTableCellRenderer());
		this.installRendererIfNecessary(Icon.class,
				new SubstanceDefaultTableCellRenderer.IconRenderer());
		// this.table.setDefaultRenderer(Icon.class,
		// new SubstanceDefaultTableCellRenderer.IconRenderer());
		this.installRendererIfNecessary(ImageIcon.class,
				new SubstanceDefaultTableCellRenderer.IconRenderer());
		// this.table.setDefaultRenderer(ImageIcon.class,
		// new SubstanceDefaultTableCellRenderer.IconRenderer());
		this.installRendererIfNecessary(Number.class,
				new SubstanceDefaultTableCellRenderer.NumberRenderer());
		// this.table.setDefaultRenderer(Number.class,
		// new SubstanceDefaultTableCellRenderer.NumberRenderer());
		this.installRendererIfNecessary(Float.class,
				new SubstanceDefaultTableCellRenderer.DoubleRenderer());
		// this.table.setDefaultRenderer(Float.class,
		// new SubstanceDefaultTableCellRenderer.DoubleRenderer());
		this.installRendererIfNecessary(Double.class,
				new SubstanceDefaultTableCellRenderer.DoubleRenderer());
		// this.table.setDefaultRenderer(Double.class,
		// new SubstanceDefaultTableCellRenderer.DoubleRenderer());
		this.installRendererIfNecessary(Date.class,
				new SubstanceDefaultTableCellRenderer.DateRenderer());
		// this.table.setDefaultRenderer(Date.class,
		// new SubstanceDefaultTableCellRenderer.DateRenderer());
		// fix for bug 56 - making default renderer for Boolean a check box.
		this.installRendererIfNecessary(Boolean.class,
				new SubstanceDefaultTableCellRenderer.BooleanRenderer());
		// this.table.setDefaultRenderer(Boolean.class,
		// new SubstanceDefaultTableCellRenderer.BooleanRenderer());

		// Map<TableCellId, Object> selected = new HashMap<TableCellId,
		// Object>();
		int rows = this.table.getRowCount();
		int cols = this.table.getColumnCount();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (this.table.isCellSelected(i, j)) {
					TableCellId cellId = new TableCellId(i, j);
					this.selectedIndices.put(cellId, this.table
							.getValueAt(i, j));
					this.prevStateMap.put(cellId, ComponentState.SELECTED);
				}
			}
		}

		// This is a little tricky, and hopefully will not
		// interfere with existing applications. The row height in tables
		// is computed differently from trees and lists. While lists
		// trees respect the current renderers and their insets, the
		// JTable uses hard-code value of 16 pixels as the default
		// row height. This, obviously, doesn't sit well with the support
		// for custom fonts and high-DPI monitors.
		//
		// The current solution first checks whether all the renderers
		// come from Substance. If not, it does nothing. If they do, it
		// creates a dummy label, computes its preferred height and apply
		// insets. There's no need to go over each cell and compute its
		// preferred height - since at this moment the cell renderers
		// *are* Substance labels.
		boolean areAllRenderersFromSubstance = true;
		TableColumnModel columnModel = table.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			TableColumn column = columnModel.getColumn(i);
			TableCellRenderer renderer = column.getCellRenderer();
			if (renderer == null) {
				renderer = table.getDefaultRenderer(table.getColumnClass(i));
			}
			if ((renderer instanceof SubstanceDefaultTableCellRenderer)
					|| (renderer instanceof SubstanceDefaultTableCellRenderer.BooleanRenderer))
				continue;
			areAllRenderersFromSubstance = false;
			break;
		}
		if (areAllRenderersFromSubstance) {
			Insets rendererInsets = SubstanceSizeUtils
					.getTableCellRendererInsets(SubstanceSizeUtils
							.getComponentFontSize(table));
			JLabel dummy = new JLabel("dummy");
			dummy.setFont(table.getFont());
			int rowHeight = dummy.getPreferredSize().height
					+ rendererInsets.bottom + rendererInsets.top;
			table.setRowHeight(rowHeight);
		}
		this.table.putClientProperty(SubstanceCoreUtilities.USE_HIGHLIGHT,
				Boolean.TRUE);

		// instead of computing the cell renderer insets on
		// every cell rendering, compute it once and expose to the
		// SubstanceDefaultTableCellRenderer
		this.cellRendererInsets = SubstanceSizeUtils
				.getTableCellRendererInsets(SubstanceSizeUtils
						.getComponentFontSize(table));
	}

	/**
	 * Installs Substance-specific renderers for column classes that don't have
	 * application-specific renderers installed by the user code.
	 * 
	 * @param clazz
	 *            Column class.
	 * @param renderer
	 *            Default renderer for the specified column class.
	 */
	protected void installRendererIfNecessary(Class<?> clazz,
			TableCellRenderer renderer) {
		TableCellRenderer currRenderer = this.table.getDefaultRenderer(clazz);
		if (currRenderer != null) {
			boolean isCore = (currRenderer instanceof DefaultTableCellRenderer.UIResource)
					|| (currRenderer.getClass().getName()
							.startsWith("javax.swing.JTable"));
			if (!isCore)
				return;
		}
		// System.out.println(clazz.getSimpleName() + " : overriding "
		// + currRenderer.getClass().getName() + "["
		// + currRenderer.hashCode() + "] with "
		// + renderer.getClass().getName() + "[" + renderer.hashCode()
		// + "]");
		this.table.setDefaultRenderer(clazz, renderer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTableUI#uninstallDefaults()
	 */
	@Override
	protected void uninstallDefaults() {
		// fix for defect 117 - need to restore default table cell
		// renderers when Substance is unset
		for (Map.Entry<Class<?>, TableCellRenderer> entry : this.defaultRenderers
				.entrySet()) {
			// this.table.setDefaultRenderer(entry.getKey(), entry.getValue());

			// fix for issue 194 - restore only those renderers that were
			// overriden by Substance.
			this.uninstallRendererIfNecessary(entry.getKey(), entry.getValue());
		}

		this.selectedIndices.clear();
		// this.table.putClientProperty(SubstanceTableUI.SELECTED_INDICES,
		// null);

		super.uninstallDefaults();
	}

	/**
	 * Uninstalls default Substance renderers that were installed in
	 * {@link #installRendererIfNecessary(Class, TableCellRenderer)}.
	 * 
	 * @param clazz
	 *            Column class.
	 * @param renderer
	 *            Renderer to restore.
	 */
	protected void uninstallRendererIfNecessary(Class<?> clazz,
			TableCellRenderer renderer) {
		TableCellRenderer currRenderer = this.table.getDefaultRenderer(clazz);
		if (currRenderer != null) {
			boolean isSubstanceRenderer = isSubstanceDefaultRenderer(currRenderer);
			if (!isSubstanceRenderer)
				return;
		}
		if (renderer instanceof Component)
			SwingUtilities.updateComponentTreeUI((Component) renderer);
		this.table.setDefaultRenderer(clazz, renderer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTableUI#installListeners()
	 */
	@Override
	protected void installListeners() {
		super.installListeners();
		this.substancePropertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (SubstanceLookAndFeel.WATERMARK_VISIBLE.equals(evt
						.getPropertyName())) {
					SubstanceTableUI.this.table
							.setOpaque(!SubstanceCoreUtilities
									.toDrawWatermark(SubstanceTableUI.this.table));
				}

				if ("columnSelectionAllowed".equals(evt.getPropertyName())
						|| "rowSelectionAllowed".equals(evt.getPropertyName())) {
					SubstanceTableUI.this.syncSelection();
				}

				if ("model".equals(evt.getPropertyName())) {
					TableModel old = (TableModel) evt.getOldValue();
					if (old != null) {
						old
								.removeTableModelListener(substanceFadeSelectionListener);
					}
					// fix for defect 291 - track changes to the table.
					table.getModel().addTableModelListener(
							substanceFadeSelectionListener);
					selectedIndices.clear();
					prevStateMap.clear();
					nextStateMap.clear();
					SubstanceTableUI.this.syncSelection();
				}

				if ("columnModel".equals(evt.getPropertyName())) {
					TableColumnModel old = (TableColumnModel) evt.getOldValue();
					if (old != null) {
						old.getSelectionModel().removeListSelectionListener(
								substanceFadeSelectionListener);
					}
					table.getColumnModel().getSelectionModel()
							.addListSelectionListener(
									substanceFadeSelectionListener);
					selectedIndices.clear();
					prevStateMap.clear();
					nextStateMap.clear();
					SubstanceTableUI.this.syncSelection();

					JTableHeader tableHeader = table.getTableHeader();
					// fix for issue 408 - table header may be null.
					if (tableHeader != null) {
						// fix for issue 309 - syncing animations on tables
						// and table headers.
						SubstanceTableHeaderUI headerUI = (SubstanceTableHeaderUI) tableHeader
								.getUI();
						headerUI.processColumnModelChangeEvent(
								(TableColumnModel) evt.getOldValue(),
								(TableColumnModel) evt.getNewValue());
					}
				}

				// fix for defect 243 - not tracking changes to selection
				// model results in incorrect selection painting on JXTreeTable
				// component from SwingX.
				if ("selectionModel".equals(evt.getPropertyName())) {
					ListSelectionModel old = (ListSelectionModel) evt
							.getOldValue();
					if (old != null) {
						old
								.removeListSelectionListener(substanceFadeSelectionListener);
					}
					table.getSelectionModel().addListSelectionListener(
							substanceFadeSelectionListener);
					selectedIndices.clear();
					prevStateMap.clear();
					nextStateMap.clear();
					SubstanceTableUI.this.syncSelection();
				}

				if ("font".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							// fix for bug 341
							if (table == null)
								return;
							table.updateUI();
						}
					});
				}

				if ("background".equals(evt.getPropertyName())) {
					// propagate application-specific background color to the
					// header.
					Color newBackgr = (Color) evt.getNewValue();
					JTableHeader header = table.getTableHeader();
					if (header != null) {
						Color headerBackground = header.getBackground();
						if (SubstanceCoreUtilities
								.canReplaceChildBackgroundColor(headerBackground)) {
							if (!(newBackgr instanceof UIResource)) {
								// Issue 450 - wrap the color in
								// SubstanceColorResource to
								// mark that it can be replaced.
								header
										.setBackground(new SubstanceColorResource(
												newBackgr));
							} else {
								header.setBackground(newBackgr);
							}
						}
					}
				}

				// fix for issue 361 - track enabled status of the table
				// and propagate to the table header
				if ("enabled".equals(evt.getPropertyName())) {
					JTableHeader header = table.getTableHeader();
					if (header != null) {
						header.setEnabled(table.isEnabled());
					}
				}
			}
		};
		this.table
				.addPropertyChangeListener(this.substancePropertyChangeListener);

		// Add listener for the selection animation
		this.substanceFadeSelectionListener = new TableStateListener();
		this.table.getSelectionModel().addListSelectionListener(
				this.substanceFadeSelectionListener);
		TableColumnModel columnModel = this.table.getColumnModel();
		columnModel.getSelectionModel().addListSelectionListener(
				this.substanceFadeSelectionListener);
		this.table.getModel().addTableModelListener(
				this.substanceFadeSelectionListener);

		// Add listener for the fade animation
		this.substanceFadeRolloverListener = new RolloverFadeListener();
		this.table.addMouseMotionListener(this.substanceFadeRolloverListener);
		this.table.addMouseListener(this.substanceFadeRolloverListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTableUI#uninstallListeners()
	 */
	@Override
	protected void uninstallListeners() {
		this.table
				.removePropertyChangeListener(this.substancePropertyChangeListener);
		this.substancePropertyChangeListener = null;

		this.table.getSelectionModel().removeListSelectionListener(
				this.substanceFadeSelectionListener);
		this.table.getColumnModel().getSelectionModel()
				.removeListSelectionListener(
						this.substanceFadeSelectionListener);
		this.table.getModel().removeTableModelListener(
				this.substanceFadeSelectionListener);
		this.substanceFadeSelectionListener = null;

		// Remove listener for the fade animation
		this.table
				.removeMouseMotionListener(this.substanceFadeRolloverListener);
		this.table.removeMouseListener(this.substanceFadeRolloverListener);
		this.substanceFadeRolloverListener = null;

		super.uninstallListeners();
	}

	/**
	 * Paint a representation of the <code>table</code> instance that was set in
	 * installUI().
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		Rectangle clip = g.getClipBounds();

		Rectangle bounds = this.table.getBounds();
		// account for the fact that the graphics has already been translated
		// into the table's bounds
		bounds.x = bounds.y = 0;

		if (this.table.getRowCount() <= 0 || this.table.getColumnCount() <= 0 ||
		// this check prevents us from painting the entire table
				// when the clip doesn't intersect our bounds at all
				!bounds.intersects(clip)) {

			return;
		}

		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width - 1, clip.y
				+ clip.height - 1);
		int rMin = this.table.rowAtPoint(upperLeft);
		int rMax = this.table.rowAtPoint(lowerRight);
		// This should never happen (as long as our bounds intersect the clip,
		// which is why we bail above if that is the case).
		if (rMin == -1) {
			rMin = 0;
		}
		// If the table does not have enough rows to fill the view we'll get -1.
		// (We could also get -1 if our bounds don't intersect the clip,
		// which is why we bail above if that is the case).
		// Replace this with the index of the last row.
		if (rMax == -1) {
			rMax = this.table.getRowCount() - 1;
		}

		boolean ltr = this.table.getComponentOrientation().isLeftToRight();
		int cMin = this.table.columnAtPoint(ltr ? upperLeft : lowerRight);
		int cMax = this.table.columnAtPoint(ltr ? lowerRight : upperLeft);
		// This should never happen.
		if (cMin == -1) {
			cMin = 0;
		}
		// If the table does not have enough columns to fill the view we'll get
		// -1.
		// Replace this with the index of the last column.
		if (cMax == -1) {
			cMax = this.table.getColumnCount() - 1;
		}

		// Paint the cells.
		this.paintCells(g, rMin, rMax, cMin, cMax);

		// Paint the grid.
		this.paintGrid(g, rMin, rMax, cMin, cMax);

		// Paint the drop lines
		this.paintDropLines(g);
	}

	/**
	 * Paints the grid lines within <I>aRect</I>, using the grid color set with
	 * <I>setGridColor</I>. Paints vertical lines if
	 * <code>getShowVerticalLines()</code> returns true and paints horizontal
	 * lines if <code>getShowHorizontalLines()</code> returns true.
	 */
	private void paintGrid(Graphics g, int rMin, int rMax, int cMin, int cMax) {
		Graphics2D g2d = (Graphics2D) g.create();
		ComponentState currState = this.table.isEnabled() ? ComponentState.DEFAULT
				: ComponentState.DISABLED_UNSELECTED;
		float alpha = SubstanceColorSchemeUtilities.getAlpha(this.table,
				currState);
		g2d.setComposite(TransitionLayout.getAlphaComposite(this.table, alpha,
				g));

		Color gridColor = this.table.getGridColor();
		if (gridColor instanceof UIResource) {
			SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
					.getColorScheme(this.table,
							ColorSchemeAssociationKind.BORDER, this.table
									.isEnabled() ? ComponentState.DEFAULT
									: ComponentState.DISABLED_UNSELECTED);
			gridColor = scheme.getLineColor();
		}
		g2d.setColor(gridColor);

		Rectangle minCell = this.table.getCellRect(rMin, cMin, true);
		Rectangle maxCell = this.table.getCellRect(rMax, cMax, true);
		Rectangle damagedArea = minCell.union(maxCell);

		float strokeWidth = SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(this.table));
		g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (this.table.getShowHorizontalLines()) {
			int tableWidth = damagedArea.x + damagedArea.width;
			int y = damagedArea.y;
			for (int row = rMin; row <= rMax; row++) {
				y += this.table.getRowHeight(row);
				g2d.drawLine(damagedArea.x, y - 1, tableWidth - 1, y - 1);
			}
		}
		if (this.table.getShowVerticalLines()) {
			TableColumnModel cm = this.table.getColumnModel();
			int tableHeight = damagedArea.y + damagedArea.height;
			int x;
			if (this.table.getComponentOrientation().isLeftToRight()) {
				x = damagedArea.x;
				for (int column = cMin; column <= cMax; column++) {
					int w = cm.getColumn(column).getWidth();
					x += w;
					if (column != (cm.getColumnCount() - 1)) {
						g2d.drawLine(x - 1, 0, x - 1, tableHeight - 1);
					}
				}
			} else {
				x = damagedArea.x + damagedArea.width;
				// fix for defect 196 - proper grid painting on RTL tables
				for (int column = cMin; column <= cMax; column++) {
					g2d.drawLine(x - 1, 0, x - 1, tableHeight - 1);
					int w = cm.getColumn(column).getWidth();
					x -= w;
				}
				// x -= cm.getColumn(cMax).getWidth();
				g2d.drawLine(x, 0, x, tableHeight - 1);
			}
		}
		g2d.dispose();
	}

	private int viewIndexForColumn(TableColumn aColumn) {
		TableColumnModel cm = this.table.getColumnModel();
		for (int column = 0; column < cm.getColumnCount(); column++) {
			if (cm.getColumn(column) == aColumn) {
				return column;
			}
		}
		return -1;
	}

	private void paintCells(Graphics g, int rMin, int rMax, int cMin, int cMax) {
		JTableHeader header = this.table.getTableHeader();
		TableColumn draggedColumn = (header == null) ? null : header
				.getDraggedColumn();

		TableColumnModel cm = this.table.getColumnModel();
		int columnMargin = cm.getColumnMargin();
		int rowMargin = this.table.getRowMargin();

		Rectangle cellRect;
		Rectangle highlightCellRect;
		TableColumn aColumn;
		int columnWidth;
		if (this.table.getComponentOrientation().isLeftToRight()) {
			for (int row = rMin; row <= rMax; row++) {
				cellRect = this.table.getCellRect(row, cMin, false);
				highlightCellRect = new Rectangle(cellRect);
				// if (!this.table.getShowHorizontalLines()) {
				// cellRect.y -= this.table.getRowMargin() / 2;
				// cellRect.height += this.table.getRowMargin();
				// }
				highlightCellRect.y -= rowMargin / 2;
				highlightCellRect.height += rowMargin;

				for (int column = cMin; column <= cMax; column++) {
					aColumn = cm.getColumn(column);
					columnWidth = aColumn.getWidth();

					cellRect.width = columnWidth - columnMargin;
					highlightCellRect.x = cellRect.x - columnMargin / 2;
					highlightCellRect.width = columnWidth;

					if (aColumn != draggedColumn) {
						this.paintCell(g, cellRect, highlightCellRect, row,
								column);
					}
					cellRect.x += columnWidth;
				}
			}
		} else {
			for (int row = rMin; row <= rMax; row++) {
				cellRect = this.table.getCellRect(row, cMin, false);
				highlightCellRect = new Rectangle(cellRect);
				highlightCellRect.y -= rowMargin / 2;
				highlightCellRect.height += rowMargin;

				aColumn = cm.getColumn(cMin);

				if (aColumn != draggedColumn) {
					columnWidth = aColumn.getWidth();
					cellRect.width = columnWidth - columnMargin;

					highlightCellRect.x = cellRect.x - columnMargin / 2;
					highlightCellRect.width = columnWidth;

					this.paintCell(g, cellRect, highlightCellRect, row, cMin);
				}
				for (int column = cMin + 1; column <= cMax; column++) {
					aColumn = cm.getColumn(column);
					columnWidth = aColumn.getWidth();
					cellRect.width = columnWidth - columnMargin;
					cellRect.x -= columnWidth;

					highlightCellRect.x = cellRect.x - columnMargin / 2;
					highlightCellRect.width = columnWidth;
					if (aColumn != draggedColumn) {
						this.paintCell(g, cellRect, highlightCellRect, row,
								column);
					}
				}
			}
		}

		// Paint the dragged column if we are dragging.
		if (draggedColumn != null) {
			Graphics2D g2d = (Graphics2D) g.create();
			// enhancement 331 - translucent dragged column
			g2d.setComposite(TransitionLayout.getAlphaComposite(this.table,
					0.65f, g));
			this.paintDraggedArea(g2d, rMin, rMax, draggedColumn, header
					.getDraggedDistance());
			g2d.dispose();
		}

		// Remove any renderers that may be left in the rendererPane.
		this.rendererPane.removeAll();
	}

	private void paintDraggedArea(Graphics g, int rMin, int rMax,
			TableColumn draggedColumn, int distance) {
		int draggedColumnIndex = this.viewIndexForColumn(draggedColumn);

		Rectangle minCell = this.table.getCellRect(rMin, draggedColumnIndex,
				true);
		Rectangle maxCell = this.table.getCellRect(rMax, draggedColumnIndex,
				true);

		Rectangle vacatedColumnRect = minCell.union(maxCell);

		// Paint a gray well in place of the moving column.
		g.setColor(this.table.getParent().getBackground());
		g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
				vacatedColumnRect.width, vacatedColumnRect.height);

		// Move to the where the cell has been dragged.
		vacatedColumnRect.x += distance;

		// Fill the background.
		g.setColor(this.table.getBackground());
		g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
				vacatedColumnRect.width, vacatedColumnRect.height);

		// Paint the vertical grid lines if necessary.
		if (this.table.getShowVerticalLines()) {
			g.setColor(this.table.getGridColor());
			int x1 = vacatedColumnRect.x;
			int y1 = vacatedColumnRect.y;
			int x2 = x1 + vacatedColumnRect.width - 1;
			int y2 = y1 + vacatedColumnRect.height - 1;
			// Left
			g.drawLine(x1 - 1, y1, x1 - 1, y2);
			// Right
			g.drawLine(x2, y1, x2, y2);
		}

		for (int row = rMin; row <= rMax; row++) {
			// Render the cell value
			Rectangle r = this.table
					.getCellRect(row, draggedColumnIndex, false);
			r.x += distance;
			this.paintCell(g, r, r, row, draggedColumnIndex);

			// Paint the (lower) horizontal grid line if necessary.
			if (this.table.getShowHorizontalLines()) {
				g.setColor(this.table.getGridColor());
				Rectangle rcr = this.table.getCellRect(row, draggedColumnIndex,
						true);
				rcr.x += distance;
				int x1 = rcr.x;
				int y1 = rcr.y;
				int x2 = x1 + rcr.width - 1;
				int y2 = y1 + rcr.height - 1;
				g.drawLine(x1, y2, x2, y2);
			}
		}
	}

	protected void paintCell(Graphics g, Rectangle cellRect,
			Rectangle highlightCellRect, int row, int column) {
		// boolean isFromSubstance = (rendererComponent instanceof
		// SubstanceDefaultTableCellRenderer)
		// || (rendererComponent instanceof
		// SubstanceDefaultTableCellRenderer.BooleanRenderer);

		Graphics2D g2d = (Graphics2D) g.create();
		// fix for issue 183 - passing the original Graphics context
		// to compute the alpha composite. If the table is in a JXPanel
		// (component from SwingX) and it has custom alpha value set,
		// then the original graphics context will have a SRC_OVER
		// alpha composite applied to it.
		g2d.setComposite(TransitionLayout.getAlphaComposite(this.table, g));

		TableCellId cellId = new TableCellId(row, column);

		ComponentState prevState = this.getPrevCellState(cellId);
		ComponentState currState = this.getCellState(cellId);
		// System.out.println(cellId + ":" + prevState.name() + "->"
		// + currState.name());
		// float alphaForPrevBackground = 0.0f;

		// optimize for tables that don't initiate rollover
		// or selection animations
		FadeState state = updateInfo.hasRolloverAnimations
				|| updateInfo.hasSelectionAnimations ? SubstanceFadeUtilities
				.getFadeState(this.table, cellId, FadeKind.SELECTION,
						FadeKind.ROLLOVER) : null;

		boolean hasHighlights = (prevState != ComponentState.DEFAULT)
				|| (currState != ComponentState.DEFAULT) || (state != null);

		Set<SubstanceConstants.Side> highlightOpenSides = null;
		float highlightBorderAlpha = 0.0f;
		float totalHighlightAlpha = 0.0f;
		SubstanceColorScheme prevHighlightScheme = null;
		SubstanceColorScheme currHighlightScheme = null;
		SubstanceColorScheme prevBorderScheme = null;
		SubstanceColorScheme currBorderScheme = null;
		float fadeHighlightCoef = 0.0f;

		if (hasHighlights) {
			prevHighlightScheme = this.updateInfo
					.getHighlightColorScheme(prevState);
			currHighlightScheme = this.updateInfo
					.getHighlightColorScheme(currState);
			prevBorderScheme = this.updateInfo
					.getHighlightBorderColorScheme(prevState);
			currBorderScheme = this.updateInfo
					.getHighlightBorderColorScheme(currState);

			// Compute the alpha values for the animation.
			float startHighlightAlpha = this.updateInfo
					.getHighlightAlpha(prevState);
			float endHighlightAlpha = this.updateInfo
					.getHighlightAlpha(currState);

			totalHighlightAlpha = endHighlightAlpha;
			// System.out.println("-------- (" + System.currentTimeMillis() %
			// 10000
			// + ") " + row + ":" + column + " [" + cellRect + "] --------");
			if (state != null) {
				// System.out.println("State not null on " + row + ":" + column
				// +
				// ":"
				// + state.fadeKind + ":" + state.getFadePosition());
				// System.out.println("States : " + prevState + "->" +
				// currState);
				fadeHighlightCoef = state.getFadePosition();

				// compute the total alpha of the overlays.
				if (state.isFadingIn()) {
					totalHighlightAlpha = startHighlightAlpha
							+ (endHighlightAlpha - startHighlightAlpha)
							* fadeHighlightCoef;
				} else {
					totalHighlightAlpha = startHighlightAlpha
							+ (endHighlightAlpha - startHighlightAlpha)
							* (1.0f - fadeHighlightCoef);
				}

				if (state.isFadingIn())
					fadeHighlightCoef = 1.0f - fadeHighlightCoef;

				// System.out.println("prev alpha " + alphaForPrevBackground
				// + ", curr alpha " + alphaForCurrBackground);
				// System.out.println("from " + prevTheme.getDisplayName() +"
				// to"
				// + currTheme.getDisplayName());
			}

			// System.out.println("[" + row + ":" + column + "] from "
			// + prevTheme.getDisplayName() + "[at " + alphaForPrevBackground
			// + "] to " + currTheme.getDisplayName() + "[at "
			// + alphaForCurrBackground + "]");

			if (!updateInfo.hasSelectionAnimations
					&& (prevState.isKindActive(FadeKind.SELECTION) || currState
							.isKindActive(FadeKind.SELECTION))) {
				// no animations on selected cells in big tables - bug 209
				fadeHighlightCoef = 0.0f;
			}

			// compute the highlight visuals, but only if there are
			// highlights on this cell (optimization)
			highlightOpenSides = EnumSet.noneOf(Side.class);
			// show highlight border only when the table grid is not shown
			highlightBorderAlpha = (table.getShowHorizontalLines() || table
					.getShowVerticalLines()) ? 0.0f : 0.8f;
			if (!table.getColumnSelectionAllowed()
					&& table.getRowSelectionAllowed()) {
				// if row selection is on and column selection is off, we
				// will
				// show the highlight for the entire row

				// all cells have open left side
				highlightOpenSides.add(SubstanceConstants.Side.LEFT);
				// all cells have open right side
				highlightOpenSides.add(SubstanceConstants.Side.RIGHT);
			}
			if (table.getColumnSelectionAllowed()
					&& !table.getRowSelectionAllowed()) {
				// if row selection is off and column selection is on, we
				// will
				// show the highlight for the entire column

				// if (table.getTableHeader().isVisible() || (row > 0)) {
				// the top side is open for all rows except the
				// first, or when the table header is visible
				highlightOpenSides.add(SubstanceConstants.Side.TOP);
				// }
				// if (row < (this.table.getRowCount() - 1)) {
				// all cells but the last have open bottom side
				highlightOpenSides.add(SubstanceConstants.Side.BOTTOM);
				// }
			}
			if (row > 1) {
				ComponentState upperNeighbourState = this
						.getCellState(new TableCellId(row - 1, column));
				if (currState == upperNeighbourState) {
					// the cell above it is in the same state
					highlightOpenSides.add(SubstanceConstants.Side.TOP);
				}
			}
			if (column > 1) {
				ComponentState leftNeighbourState = this
						.getCellState(new TableCellId(row, column - 1));
				if (currState == leftNeighbourState) {
					// the cell to the left is in the same state
					highlightOpenSides.add(SubstanceConstants.Side.LEFT);
				}
			}
			// if ((row == 0) && table.getTableHeader().isVisible()) {
			// // special case for a selected cell in first row when the
			// // table header is visible - open top side
			// highlightOpenSides.add(SubstanceConstants.Side.TOP);
			// }
			if (row == 0) {
				highlightOpenSides.add(SubstanceConstants.Side.TOP);
			}
			if (row == (table.getRowCount() - 1)) {
				highlightOpenSides.add(SubstanceConstants.Side.BOTTOM);
			}
			if (column == 0) {
				highlightOpenSides.add(SubstanceConstants.Side.LEFT);
			}
			if (column == (table.getColumnCount() - 1)) {
				highlightOpenSides.add(SubstanceConstants.Side.RIGHT);
			}
		}

		boolean isRollover = ((this.rolledOverId != null) && this.rolledOverId
				.equals(cellId));
		if (this.table.isEditing() && this.table.getEditingRow() == row
				&& this.table.getEditingColumn() == column) {
			Component component = this.table.getEditorComponent();
			// if (component instanceof JComponent) {
			// ((JComponent) component).putClientProperty(
			// SubstanceCoreUtilities.USE_HIGHLIGHT, Boolean.TRUE);
			// }

			if (totalHighlightAlpha > 0.0f) {
				g2d.setComposite(TransitionLayout.getAlphaComposite(this.table,
						totalHighlightAlpha, g));
				HighlightPainterUtils.paintHighlight(g2d, this.rendererPane,
						component, highlightCellRect, highlightBorderAlpha,
						highlightOpenSides, currHighlightScheme,
						prevHighlightScheme, currBorderScheme,
						prevBorderScheme, fadeHighlightCoef);
				g2d.setComposite(TransitionLayout.getAlphaComposite(this.table,
						g));
			}

			if (component instanceof JComponent) {
				// Play with opacity to make our own gradient background
				// on selected elements to show.
				//
				// Compute the selection status to prevent flicker - JTable
				// registers a listener on selection changes and repaints
				// the relevant cell before our listener (in TableUI) gets
				// the chance to start the fade sequence. The result is that
				// the first frame uses full opacity, and the next frame
				// starts the fade sequence. So, we use the UI delegate to
				// compute the selection status.
				boolean newOpaque = !(this.selectedIndices.containsKey(cellId)
						|| isRollover || (state != null));
				if (this.updateInfo.toDrawWatermark)
					// SubstanceCoreUtilities.toDrawWatermark(this.table))
					newOpaque = false;

				this.updateInfo.opacity.clear();
				// System.out.println("Pre-painting at index " + row + " ["
				// +
				// value
				// + "] " + (rendererComponent.isOpaque() ? "opaque" :
				// "transparent")
				// + " with bg " + rendererComponent.getBackground());
				if (!newOpaque)
					SubstanceCoreUtilities.makeNonOpaque(component,
							this.updateInfo.opacity);
				// System.out.println("Painting "
				// + (newOpaque ? "opaque" : "transparent")
				// + " with bg " + component.getBackground());
				component.setBounds(cellRect);
				component.validate();
				// System.out.println("Painting at index " + row + " [" +
				// value
				// + "] " + (newOpaque ? "opaque" : "transparent")
				// + " with bg " + rendererComponent.getBackground());
				if (!newOpaque)
					SubstanceCoreUtilities.restoreOpaque(component,
							this.updateInfo.opacity);
				// System.out.println("Post-painting at index " + row + " ["
				// +
				// value
				// + "] " + (rendererComponent.isOpaque() ? "opaque" :
				// "transparent")
				// + " with bg " + rendererComponent.getBackground());
			} else {
				component.setBounds(cellRect);
				component.validate();
			}

		} else {
			// if (rendererComponent instanceof JComponent) {
			// ((JComponent) rendererComponent).putClientProperty(
			// SubstanceCoreUtilities.USE_HIGHLIGHT, Boolean.TRUE);
			// }

			TableCellRenderer renderer = this.table
					.getCellRenderer(row, column);
			boolean isSubstanceRenderer = isSubstanceDefaultRenderer(renderer);
			Component rendererComponent = this.table.prepareRenderer(renderer,
					row, column);
			boolean isWatermarkBleed = this.updateInfo.toDrawWatermark;
			if (rendererComponent != null) {
				if (!isWatermarkBleed) {
					Color background = rendererComponent.getBackground();
					// optimization - only render background if it's different
					// from the table background
					if ((background != null)
							&& (!table.getBackground().equals(background) || this.updateInfo.isInDecorationArea)) {
						// fill with the renderer background color
						g2d.setColor(background);
						g2d.fillRect(highlightCellRect.x, highlightCellRect.y,
								highlightCellRect.width,
								highlightCellRect.height);
					}
				} else {
					BackgroundPaintingUtils.fillAndWatermark(g2d, this.table,
							rendererComponent.getBackground(),
							highlightCellRect);
				}
			}

			if (isSubstanceRenderer && (totalHighlightAlpha > 0.0f)) {
				g2d.setComposite(TransitionLayout.getAlphaComposite(this.table,
						totalHighlightAlpha, g));
				float extra = SubstanceSizeUtils
						.getBorderStrokeWidth(SubstanceSizeUtils
								.getComponentFontSize(this.table
										.getTableHeader()));
				float extraWidth = highlightOpenSides
						.contains(SubstanceConstants.Side.LEFT) ? 0.0f : extra;
				float extraHeight = highlightOpenSides
						.contains(SubstanceConstants.Side.TOP) ? 0.0f : extra;
				// if ((column > 0) && isFocusedCell(row, column - 1))
				// extraWidth = 0.0f;
				// if ((row > 0) && isFocusedCell(row - 1, column))
				// extraHeight = 0.0f;
				// System.out
				// .println(row + ":" + column + ":"
				// + rendererComponent.getBackground() + ":"
				// + totalAlpha + ":"
				// + currTheme.getDisplayName() + ":"
				// + prevTheme.getDisplayName() + ":"
				// + fadeCoef);
				HighlightPainterUtils.paintHighlight(g2d, this.rendererPane,
						rendererComponent, new Rectangle(highlightCellRect.x
								- (int) extraWidth, highlightCellRect.y
								- (int) extraHeight, highlightCellRect.width
								+ (int) extraWidth, highlightCellRect.height
								+ (int) extraHeight), highlightBorderAlpha,
						highlightOpenSides, currHighlightScheme,
						prevHighlightScheme, currBorderScheme,
						prevBorderScheme, fadeHighlightCoef);
				g2d.setComposite(TransitionLayout.getAlphaComposite(this.table,
						g));
			}

			if (rendererComponent instanceof JComponent) {
				// Play with opacity to make our own gradient background
				// on selected elements to show.
				JComponent jRenderer = (JComponent) rendererComponent;
				// Compute the selection status to prevent flicker - JTable
				// registers a listener on selection changes and repaints
				// the relevant cell before our listener (in TableUI) gets
				// the chance to start the fade sequence. The result is that
				// the first frame uses full opacity, and the next frame
				// starts the fade sequence. So, we use the UI delegate to
				// compute the selection status.
				boolean isSelected = updateInfo.hasSelectionAnimations ? this.selectedIndices
						.containsKey(cellId)
						: this.table.isCellSelected(row, column);
				boolean newOpaque = !(isSelected || isRollover || (state != null));

				if (this.updateInfo.toDrawWatermark)
					newOpaque = false;

				Map<Component, Boolean> opacity = new HashMap<Component, Boolean>();
				// System.out.println("Pre-painting at index " + row + " ["
				// +
				// value
				// + "] " + (rendererComponent.isOpaque() ? "opaque" :
				// "transparent")
				// + " with bg " + rendererComponent.getBackground());
				if (!newOpaque)
					SubstanceCoreUtilities.makeNonOpaque(jRenderer, opacity);
				// System.out.println("Painting "
				// + rendererComponent.getClass().getSimpleName()
				// + " at " + row + ":" + column + " "
				// + (newOpaque ? "opaque" : "transparent")
				// + " with bg " + rendererComponent.getBackground());
				// System.out.println(row + ":" + column + ":" + cellRect);
				// SubstanceDecorationUtilities.setDecorationType(jRenderer,
				// this.updateInfo.decorationAreaType);
				this.rendererPane.paintComponent(g2d, rendererComponent,
						this.table, cellRect.x, cellRect.y, cellRect.width,
						cellRect.height, true);
				// System.out.println("Painting at index " + row + " [" +
				// value
				// + "] " + (newOpaque ? "opaque" : "transparent")
				// + " with bg " + rendererComponent.getBackground());
				if (!newOpaque)
					SubstanceCoreUtilities.restoreOpaque(jRenderer, opacity);
				// System.out.println("Post-painting at index " + row + " ["
				// +
				// value
				// + "] " + (rendererComponent.isOpaque() ? "opaque" :
				// "transparent")
				// + " with bg " + rendererComponent.getBackground());
			} else {
				this.rendererPane.paintComponent(g2d, rendererComponent,
						this.table, cellRect.x, cellRect.y, cellRect.width,
						cellRect.height, true);
			}
		}
		// System.out
		// .println(
		// "------------------------------------------------------------------------------"
		// );
		g2d.dispose();
	}

	private void paintDropLines(Graphics g) {
		JTable.DropLocation loc = table.getDropLocation();
		if (loc == null) {
			return;
		}

		Color color = UIManager.getColor("Table.dropLineColor");
		Color shortColor = UIManager.getColor("Table.dropLineShortColor");
		if (color == null && shortColor == null) {
			return;
		}

		Rectangle rect;

		rect = getHDropLineRect(loc);
		if (rect != null) {
			int x = rect.x;
			int w = rect.width;
			if (color != null) {
				extendRect(rect, true);
				g.setColor(color);
				g.fillRect(rect.x, rect.y, rect.width, rect.height);
			}
			if (!loc.isInsertColumn() && shortColor != null) {
				g.setColor(shortColor);
				g.fillRect(x, rect.y, w, rect.height);
			}
		}

		rect = getVDropLineRect(loc);
		if (rect != null) {
			int y = rect.y;
			int h = rect.height;
			if (color != null) {
				extendRect(rect, false);
				g.setColor(color);
				g.fillRect(rect.x, rect.y, rect.width, rect.height);
			}
			if (!loc.isInsertRow() && shortColor != null) {
				g.setColor(shortColor);
				g.fillRect(rect.x, y, rect.width, h);
			}
		}
	}

	private Rectangle getHDropLineRect(JTable.DropLocation loc) {
		if (!loc.isInsertRow()) {
			return null;
		}

		int row = loc.getRow();
		int col = loc.getColumn();
		if (col >= table.getColumnCount()) {
			col--;
		}

		Rectangle rect = table.getCellRect(row, col, true);

		if (row >= table.getRowCount()) {
			row--;
			Rectangle prevRect = table.getCellRect(row, col, true);
			rect.y = prevRect.y + prevRect.height;
		}

		if (rect.y == 0) {
			rect.y = -1;
		} else {
			rect.y -= 2;
		}

		rect.height = 3;

		return rect;
	}

	private Rectangle getVDropLineRect(JTable.DropLocation loc) {
		if (!loc.isInsertColumn()) {
			return null;
		}

		boolean ltr = table.getComponentOrientation().isLeftToRight();
		int col = loc.getColumn();
		Rectangle rect = table.getCellRect(loc.getRow(), col, true);

		if (col >= table.getColumnCount()) {
			col--;
			rect = table.getCellRect(loc.getRow(), col, true);
			if (ltr) {
				rect.x = rect.x + rect.width;
			}
		} else if (!ltr) {
			rect.x = rect.x + rect.width;
		}

		if (rect.x == 0) {
			rect.x = -1;
		} else {
			rect.x -= 2;
		}

		rect.width = 3;

		return rect;
	}

	private Rectangle extendRect(Rectangle rect, boolean horizontal) {
		if (rect == null) {
			return rect;
		}

		if (horizontal) {
			rect.x = 0;
			rect.width = table.getWidth();
		} else {
			rect.y = 0;

			if (table.getRowCount() != 0) {
				Rectangle lastRect = table.getCellRect(table.getRowCount() - 1,
						0, true);
				rect.height = lastRect.y + lastRect.height;
			} else {
				rect.height = table.getHeight();
			}
		}

		return rect;
	}

	/**
	 * Repaints a single cell during the fade animation cycle.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class CellRepaintCallback extends UIThreadFadeTrackerAdapter {
		/**
		 * Associated table.
		 */
		protected JTable table;

		/**
		 * Associated (animated) row index.
		 */
		protected int rowIndex;

		/**
		 * Associated (animated) column index.
		 */
		protected int columnIndex;

		/**
		 * Creates a new animation repaint callback.
		 * 
		 * @param table
		 *            Associated table.
		 * @param rowIndex
		 *            Associated (animated) row index.
		 * @param columnIndex
		 *            Associated (animated) column index.
		 */
		public CellRepaintCallback(JTable table, int rowIndex, int columnIndex) {
			super();
			this.table = table;
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jvnet.lafwidget.utils.FadeTracker$FadeTrackerCallback#fadeEnded
		 * (org.jvnet.lafwidget.utils.FadeTracker.FadeKind)
		 */
		@Override
		public void fadeEnded(FadeKind fadeKind) {
			if ((SubstanceTableUI.this.table == this.table)
					&& (this.rowIndex < this.table.getRowCount())
					&& (this.columnIndex < this.table.getColumnCount())) {
				TableCellId cellIndex = new TableCellId(this.rowIndex,
						this.columnIndex);
				ComponentState currState = SubstanceTableUI.this
						.getCellState(cellIndex);
				// boolean isLarge = (table.getRowCount() *
				// table.getColumnCount() > 1000);
				if (currState == ComponentState.DEFAULT) {
					// || (isLarge && currState.isSelected()))
					SubstanceTableUI.this.prevStateMap.remove(cellIndex);
					SubstanceTableUI.this.nextStateMap.remove(cellIndex);
				} else {
					SubstanceTableUI.this.prevStateMap
							.put(cellIndex, currState);
					SubstanceTableUI.this.nextStateMap
							.put(cellIndex, currState);
				}
				// System.out.println(rowIndex + ":" + columnIndex + "->"
				// + prevStateMap.get(cellIndex).name());
			}
			// System.out.println("Cell - Fade ended on " + rowIndex + ":"
			// + columnIndex);
			this.repaintCell();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jvnet.lafwidget.utils.FadeTracker$FadeTrackerCallback#fadePerformed
		 * (org.jvnet.lafwidget.utils.FadeTracker.FadeKind, float)
		 */
		@Override
		public void fadePerformed(FadeKind fadeKind, float fade) {
			// System.out.println("Cell - " + fadeKind + " on " + rowIndex + ":"
			// + columnIndex + ":" + fade);
			if ((SubstanceTableUI.this.table == this.table)
					&& (this.rowIndex < this.table.getRowCount())
					&& (this.columnIndex < this.table.getColumnCount())) {
				TableCellId cellIndex = new TableCellId(this.rowIndex,
						this.columnIndex);
				SubstanceTableUI.this.nextStateMap.put(cellIndex,
						SubstanceTableUI.this.getCellState(cellIndex));
			}
			this.repaintCell();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jvnet.lafwidget.animation.FadeTrackerAdapter#fadeReversed(org
		 * .jvnet.lafwidget.animation.FadeKind, boolean, float)
		 */
		@Override
		public void fadeReversed(FadeKind fadeKind, boolean isFadingIn,
				float fadeCycle10) {
			if ((SubstanceTableUI.this.table == this.table)
					&& (this.rowIndex < this.table.getRowCount())
					&& (this.columnIndex < this.table.getColumnCount())) {
				TableCellId cellIndex = new TableCellId(this.rowIndex,
						this.columnIndex);
				ComponentState nextState = SubstanceTableUI.this.nextStateMap
						.get(cellIndex);
				if (nextState == null) {
					SubstanceTableUI.this.prevStateMap.remove(cellIndex);
				} else {
					SubstanceTableUI.this.prevStateMap
							.put(cellIndex, nextState);
				}
				// System.out.println(tabIndex + "->"
				// + prevStateMap.get(tabIndex).name());
			}
			this.repaintCell();
		}

		/**
		 * Repaints the associated cell.
		 */
		private void repaintCell() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (SubstanceTableUI.this.table == null) {
						// may happen if the LAF was switched in the meantime
						return;
					}
					int rowCount = CellRepaintCallback.this.table.getRowCount();
					int colCount = CellRepaintCallback.this.table
							.getColumnCount();
					if ((rowCount > 0)
							&& (CellRepaintCallback.this.rowIndex < rowCount)
							&& (colCount > 0)
							&& (CellRepaintCallback.this.columnIndex < colCount)) {
						// need to retrieve the cell rectangle since the cells
						// can be moved while animating
						Rectangle rect = CellRepaintCallback.this.table
								.getCellRect(CellRepaintCallback.this.rowIndex,
										CellRepaintCallback.this.columnIndex,
										true);

						if (!table.getShowHorizontalLines()
								&& !table.getShowVerticalLines()) {
							float extra = SubstanceSizeUtils
									.getBorderStrokeWidth(SubstanceSizeUtils
											.getComponentFontSize(table
													.getTableHeader()));
							rect.x -= (int) extra;
							rect.width += 2 * (int) extra;
							rect.y -= (int) extra;
							rect.height += 2 * (int) extra;
						}
						// System.out.println("Cell Repainting " + rowIndex +
						// ":"
						// + columnIndex + ":" + rect);
						CellRepaintCallback.this.table.repaint(rect);
					}
				}
			});
		}
	}

	/**
	 * Repaints a single row during the fade animation cycle.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class RowRepaintCallback extends UIThreadFadeTrackerAdapter {
		/**
		 * Associated table.
		 */
		protected JTable table;

		/**
		 * Associated (animated) row index.
		 */
		protected int rowIndex;

		/**
		 * Creates a new animation repaint callback.
		 * 
		 * @param table
		 *            Associated table.
		 * @param rowIndex
		 *            Associated (animated) row index.
		 */
		public RowRepaintCallback(JTable table, int rowIndex) {
			super();
			this.table = table;
			this.rowIndex = rowIndex;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jvnet.lafwidget.utils.FadeTracker$FadeTrackerCallback#fadeEnded
		 * (org.jvnet.lafwidget.utils.FadeTracker.FadeKind)
		 */
		@Override
		public void fadeEnded(FadeKind fadeKind) {
			if ((SubstanceTableUI.this.table == this.table)
					&& (this.rowIndex < this.table.getRowCount())) {
				for (int columnIndex = 0; columnIndex < this.table
						.getColumnCount(); columnIndex++) {
					TableCellId cellIndex = new TableCellId(this.rowIndex,
							columnIndex);
					ComponentState currState = SubstanceTableUI.this
							.getCellState(cellIndex);
					// boolean isLarge = (table.getRowCount()
					// * table.getColumnCount() > 1000);
					if (currState == ComponentState.DEFAULT) {
						// || (isLarge && currState.isSelected()))
						SubstanceTableUI.this.prevStateMap.remove(cellIndex);
						SubstanceTableUI.this.nextStateMap.remove(cellIndex);
					} else {
						SubstanceTableUI.this.prevStateMap.put(cellIndex,
								currState);
						SubstanceTableUI.this.nextStateMap.put(cellIndex,
								currState);
					}
				}
				// System.out.println(tabIndex + "->"
				// + prevStateMap.get(tabIndex).name());
			}
			// System.out.println("Fade ended on " + rowIndex);
			this.repaintRow();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jvnet.lafwidget.utils.FadeTracker$FadeTrackerCallback#fadePerformed
		 * (org.jvnet.lafwidget.utils.FadeTracker.FadeKind, float)
		 */
		@Override
		public void fadePerformed(FadeKind fadeKind, float fade) {
			// System.out.println("Fade on " + rowIndex + ":" + fade);
			if ((SubstanceTableUI.this.table == this.table)
					&& (this.rowIndex < this.table.getRowCount())) {
				for (int columnIndex = 0; columnIndex < this.table
						.getColumnCount(); columnIndex++) {
					TableCellId cellIndex = new TableCellId(this.rowIndex,
							columnIndex);
					SubstanceTableUI.this.nextStateMap.put(cellIndex,
							SubstanceTableUI.this.getCellState(cellIndex));
				}
			}
			this.repaintRow();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jvnet.lafwidget.animation.FadeTrackerAdapter#fadeReversed(org
		 * .jvnet.lafwidget.animation.FadeKind, boolean, float)
		 */
		@Override
		public void fadeReversed(FadeKind fadeKind, boolean isFadingIn,
				float fadeCycle10) {
			if ((SubstanceTableUI.this.table == this.table)
					&& (this.rowIndex < this.table.getRowCount())) {
				for (int columnIndex = 0; columnIndex < this.table
						.getColumnCount(); columnIndex++) {
					TableCellId cellIndex = new TableCellId(this.rowIndex,
							columnIndex);
					ComponentState nextState = SubstanceTableUI.this.nextStateMap
							.get(cellIndex);
					if (nextState == null) {
						SubstanceTableUI.this.prevStateMap.remove(cellIndex);
					} else {
						SubstanceTableUI.this.prevStateMap.put(cellIndex,
								nextState);
					}
					// System.out.println(tabIndex + "->"
					// + prevStateMap.get(tabIndex).name());
				}
			}
			this.repaintRow();
		}

		/**
		 * Repaints the associated row.
		 */
		private void repaintRow() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (SubstanceTableUI.this.table == null) {
						// may happen if the LAF was switched in the meantime
						return;
					}
					int rowCount = RowRepaintCallback.this.table.getRowCount();
					if ((rowCount > 0)
							&& (RowRepaintCallback.this.rowIndex < rowCount)) {
						// need to retrieve the cell rectangle since the cells
						// can be moved while animating
						Rectangle rect = RowRepaintCallback.this.table
								.getCellRect(RowRepaintCallback.this.rowIndex,
										0, true);
						for (int i = 1; i < RowRepaintCallback.this.table
								.getColumnCount(); i++) {
							rect = rect.union(RowRepaintCallback.this.table
									.getCellRect(
											RowRepaintCallback.this.rowIndex,
											i, true));
						}
						if (!table.getShowHorizontalLines()
								&& !table.getShowVerticalLines()) {
							float extra = SubstanceSizeUtils
									.getBorderStrokeWidth(SubstanceSizeUtils
											.getComponentFontSize(table
													.getTableHeader()));
							rect.y -= (int) extra;
							rect.height += 2 * (int) extra;
						}
						// System.out.println("Repainting row " + rowIndex
						// + " at " + rect);
						RowRepaintCallback.this.table.repaint(rect);
					}
				}
			});
		}
	}

	/**
	 * Repaints a single column during the fade animation cycle.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class ColumnRepaintCallback extends UIThreadFadeTrackerAdapter {
		/**
		 * Associated table.
		 */
		protected JTable table;

		/**
		 * Associated (animated) column index.
		 */
		protected int columnIndex;

		/**
		 * Creates a new animation repaint callback.
		 * 
		 * @param table
		 *            Associated table.
		 * @param columnIndex
		 *            Associated (animated) column index.
		 */
		public ColumnRepaintCallback(JTable table, int columnIndex) {
			super();
			this.table = table;
			this.columnIndex = columnIndex;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jvnet.lafwidget.utils.FadeTracker$FadeTrackerCallback#fadeEnded
		 * (org.jvnet.lafwidget.utils.FadeTracker.FadeKind)
		 */
		@Override
		public void fadeEnded(FadeKind fadeKind) {
			if ((SubstanceTableUI.this.table == this.table)
					&& (this.columnIndex < this.table.getColumnCount())) {
				for (int rowIndex = 0; rowIndex < this.table.getRowCount(); rowIndex++) {
					TableCellId cellIndex = new TableCellId(rowIndex,
							this.columnIndex);
					ComponentState currState = SubstanceTableUI.this
							.getCellState(cellIndex);
					// boolean isLarge = (table.getRowCount()
					// * table.getColumnCount() > 1000);
					if (currState == ComponentState.DEFAULT) {
						// || (isLarge && currState.isSelected()))
						SubstanceTableUI.this.prevStateMap.remove(cellIndex);
						SubstanceTableUI.this.nextStateMap.remove(cellIndex);
					} else {
						SubstanceTableUI.this.prevStateMap.put(cellIndex,
								currState);
						SubstanceTableUI.this.nextStateMap.put(cellIndex,
								currState);
					}
				}
				// System.out.println(tabIndex + "->"
				// + prevStateMap.get(tabIndex).name());
			}
			this.repaintColumn();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jvnet.lafwidget.animation.FadeTrackerAdapter#fadeReversed(org
		 * .jvnet.lafwidget.animation.FadeKind, boolean, float)
		 */
		@Override
		public void fadeReversed(FadeKind fadeKind, boolean isFadingIn,
				float fadeCycle10) {
			if ((SubstanceTableUI.this.table == this.table)
					&& (this.columnIndex < this.table.getColumnCount())) {
				for (int rowIndex = 0; rowIndex < this.table.getRowCount(); rowIndex++) {
					TableCellId cellIndex = new TableCellId(rowIndex,
							this.columnIndex);
					ComponentState nextState = SubstanceTableUI.this.nextStateMap
							.get(cellIndex);
					if (nextState == null) {
						SubstanceTableUI.this.prevStateMap.remove(cellIndex);
					} else {
						SubstanceTableUI.this.prevStateMap.put(cellIndex,
								nextState);
					}
					// System.out.println(tabIndex + "->"
					// + prevStateMap.get(tabIndex).name());
				}
			}
			this.repaintColumn();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jvnet.lafwidget.utils.FadeTracker$FadeTrackerCallback#fadePerformed
		 * (org.jvnet.lafwidget.utils.FadeTracker.FadeKind, float)
		 */
		@Override
		public void fadePerformed(FadeKind fadeKind, float fade) {
			if ((SubstanceTableUI.this.table == this.table)
					&& (this.columnIndex < this.table.getColumnCount())) {
				for (int rowIndex = 0; rowIndex < this.table.getRowCount(); rowIndex++) {
					TableCellId cellIndex = new TableCellId(rowIndex,
							this.columnIndex);
					SubstanceTableUI.this.nextStateMap.put(cellIndex,
							SubstanceTableUI.this.getCellState(cellIndex));
				}
			}
			this.repaintColumn();
		}

		/**
		 * Repaints the associated row.
		 */
		private void repaintColumn() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (SubstanceTableUI.this.table == null) {
						// may happen if the LAF was switched in the meantime
						return;
					}
					int columnCount = ColumnRepaintCallback.this.table
							.getColumnCount();
					if ((columnCount > 0)
							&& (ColumnRepaintCallback.this.columnIndex < columnCount)) {
						// need to retrieve the cell rectangle since the cells
						// can be moved while animating
						Rectangle rect = ColumnRepaintCallback.this.table
								.getCellRect(0,
										ColumnRepaintCallback.this.columnIndex,
										true);
						for (int i = 1; i < ColumnRepaintCallback.this.table
								.getRowCount(); i++) {
							rect = rect
									.union(ColumnRepaintCallback.this.table
											.getCellRect(
													i,
													ColumnRepaintCallback.this.columnIndex,
													true));
						}
						if (!table.getShowHorizontalLines()
								&& !table.getShowVerticalLines()) {
							float extra = SubstanceSizeUtils
									.getBorderStrokeWidth(SubstanceSizeUtils
											.getComponentFontSize(table
													.getTableHeader()));
							rect.x -= (int) extra;
							rect.width += 2 * (int) extra;
						}
						ColumnRepaintCallback.this.table.repaint(rect);
					}
				}
			});
		}
	}

	public static interface TableId extends Comparable {
		public TableId cloneId();
	}

	/**
	 * ID of a single table cell.
	 * 
	 * @author Kirill Grouchnikov
	 */
	@SuppressWarnings("unchecked")
	public static class TableCellId implements TableId {
		/**
		 * Cell row.
		 */
		protected int row;

		/**
		 * Cell column.
		 */
		protected int column;

		/**
		 * Indicates whether the comparison ({@link #equals(Object)}) should
		 * return <code>false</code> when it is passed either
		 * {@link TableColumnId} or {@link TableRowId}.
		 */
		protected boolean isExactComparison;

		/**
		 * Creates a new cell ID.
		 * 
		 * @param row
		 *            Cell row.
		 * @param column
		 *            Cell column.
		 */
		public TableCellId(int row, int column) {
			this.row = row;
			this.column = column;
		}

		/**
		 * Sets the comparison flag.
		 * 
		 * @param isExactComparison
		 *            If <code>true</code>, the ({@link #equals(Object)}) will
		 *            return <code>false</code> when it is passed either
		 *            {@link TableColumnId} or {@link TableRowId}.
		 */
		public void setExactComparison(boolean isExactComparison) {
			this.isExactComparison = isExactComparison;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) {
			if (o instanceof TableCellId) {
				TableCellId otherId = (TableCellId) o;
				if ((this.row == otherId.row)
						&& (this.column == otherId.column))
					return 0;
				return 1;
			}
			if (!this.isExactComparison) {
				if (o instanceof TableRowId) {
					TableRowId otherId = (TableRowId) o;
					if (this.row == otherId.row)
						return 0;
					return 1;
				}
				if (o instanceof TableColumnId) {
					TableColumnId otherId = (TableColumnId) o;
					if (this.column == otherId.column)
						return 0;
					return 1;
				}
			}
			return -1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			return this.compareTo(obj) == 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return (this.row ^ (this.row >>> 32))
					& (this.column ^ (this.column >>> 32));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.row + ":" + this.column;
		}

		@Override
		public TableId cloneId() {
			return new TableCellId(row, column);
		}
	}

	/**
	 * ID of a single table column.
	 * 
	 * @author Kirill Grouchnikov
	 */
	@SuppressWarnings("unchecked")
	protected static class TableColumnId implements TableId {
		/**
		 * Column.
		 */
		protected int column;

		/**
		 * Creates a new column ID.
		 * 
		 * @param column
		 *            Column.
		 */
		public TableColumnId(int column) {
			this.column = column;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) {
			if (o instanceof TableCellId) {
				TableCellId otherId = (TableCellId) o;
				if (this.column == otherId.column)
					return 0;
				return 1;
			}
			if (o instanceof TableColumnId) {
				TableColumnId otherId = (TableColumnId) o;
				if (this.column == otherId.column)
					return 0;
				return 1;
			}
			return -1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			return this.compareTo(obj) == 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return (this.column ^ (this.column >>> 32));
		}

		@Override
		public TableId cloneId() {
			return new TableColumnId(this.column);
		}
	}

	/**
	 * ID of a single table row.
	 * 
	 * @author Kirill Grouchnikov
	 */
	@SuppressWarnings("unchecked")
	protected static class TableRowId implements TableId {
		/**
		 * Row.
		 */
		protected int row;

		/**
		 * Creates a new row ID.
		 * 
		 * @param row
		 *            Row.
		 */
		public TableRowId(int row) {
			this.row = row;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) {
			if (o instanceof TableCellId) {
				TableCellId otherId = (TableCellId) o;
				if (this.row == otherId.row)
					return 0;
				return 1;
			}
			if (o instanceof TableRowId) {
				TableRowId otherId = (TableRowId) o;
				if (this.row == otherId.row)
					return 0;
				return 1;
			}
			return -1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			return this.compareTo(obj) == 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return (this.row ^ (this.row >>> 32));
		}

		@Override
		public TableId cloneId() {
			return new TableRowId(this.row);
		}
	}

	/**
	 * State listener for tracking the selection changes.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class TableStateListener implements ListSelectionListener,
			TableModelListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.
		 * event.ListSelectionEvent)
		 */
		@SuppressWarnings("unchecked")
		public void valueChanged(final ListSelectionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					syncSelection();
				}
			});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.event.TableModelListener#tableChanged(javax.swing.event
		 * .TableModelEvent)
		 */
		public void tableChanged(final TableModelEvent e) {
			// fix for defect 291 - tracking changes to the table.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// fix for defect 350 - font might have been
					// switched in the middle of update
					if (table == null)
						return;

					// fix for defect 328 - do not clear the
					// internal selection and focus tracking
					// when the event is table update.
					if (e.getType() != TableModelEvent.UPDATE) {
						selectedIndices.clear();
						prevStateMap.clear();
						nextStateMap.clear();
						focusedColumn = -1;
						focusedRow = -1;
					}
					syncSelection();
					table.repaint();
				}
			});
		}
	}

	/**
	 * Listener for fade animations on table rollovers.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private class RolloverFadeListener implements MouseListener,
			MouseMotionListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
			// if (SubstanceCoreUtilities.toBleedWatermark(list))
			// return;

			if (!SubstanceTableUI.this.table.isEnabled())
				return;
			// synchronized (SubstanceTableUI.this.table) {
			this.fadeOut();
			this.fadeOutTableHeader();
			// System.out.println("Nulling RO index");
			SubstanceTableUI.this.rolledOverId = null;
			SubstanceTableUI.this.rolledOverColumn = -1;
			// }
		}

		public void mouseMoved(MouseEvent e) {
			// if (SubstanceCoreUtilities.toBleedWatermark(list))
			// return;

			if (!SubstanceTableUI.this.table.isEnabled())
				return;
			this.handleMove(e);
			this.handleMoveForHeader(e);
		}

		public void mouseDragged(MouseEvent e) {
			// if (SubstanceCoreUtilities.toBleedWatermark(list))
			// return;

			if (!SubstanceTableUI.this.table.isEnabled())
				return;
			this.handleMove(e);
			this.handleMoveForHeader(e);
		}

		/**
		 * Handles various mouse move events and initiates the fade animation if
		 * necessary.
		 * 
		 * @param e
		 *            Mouse event.
		 */
		private void handleMove(MouseEvent e) {
			// synchronized (SubstanceTableUI.this.table) {
			int row = SubstanceTableUI.this.table.rowAtPoint(e.getPoint());
			int column = SubstanceTableUI.this.table
					.columnAtPoint(e.getPoint());
			if ((row < 0) || (row >= SubstanceTableUI.this.table.getRowCount())
					|| (column < 0)
					|| (column >= SubstanceTableUI.this.table.getColumnCount())) {
				this.fadeOut();
				// System.out.println("Nulling RO index");
				// table.putClientProperty(ROLLED_OVER_INDEX, null);
				SubstanceTableUI.this.rolledOverId = null;
			} else {
				// check if this is the same index

				// need to clone ID since it can go to the fade tracker
				// and be set as the rollover id
				TableId newId = SubstanceTableUI.this.getId(row, column)
						.cloneId();
				// Comparable currId = (Comparable) table
				// .getClientProperty(ROLLED_OVER_INDEX);
				if ((SubstanceTableUI.this.rolledOverId != null)
						&& newId.equals(SubstanceTableUI.this.rolledOverId))
					return;

				this.fadeOut();
				FadeTrackerCallback callback = SubstanceTableUI.this
						.getCallback(row, column);
				if (SubstanceTableUI.this._hasRolloverAnimations()) {
					FadeTracker.getInstance()
							.trackFadeIn(FadeKind.ROLLOVER,
									SubstanceTableUI.this.table, newId, false,
									callback);
				} else {
					callback.fadeEnded(FadeKind.ROLLOVER);
				}
				// System.out.println("Setting RO index to " + roIndex);
				if (FadeConfigurationManager.getInstance().fadeAllowed(
						FadeKind.ROLLOVER, SubstanceTableUI.this.table)) {
					SubstanceTableUI.this.rolledOverId = newId;
					// table.putClientProperty(ROLLED_OVER_INDEX, newId);
				}
			}
			// }
		}

		/**
		 * Handles various mouse move events and initiates the fade animation if
		 * necessary.
		 * 
		 * @param e
		 *            Mouse event.
		 */
		private void handleMoveForHeader(MouseEvent e) {
			if (!SubstanceTableUI.this.table.getColumnSelectionAllowed())
				return;
			JTableHeader header = SubstanceTableUI.this.table.getTableHeader();
			if ((header == null) || (!header.isVisible()))
				return;

			TableHeaderUI ui = header.getUI();
			if (!(ui instanceof SubstanceTableHeaderUI))
				return;

			SubstanceTableHeaderUI sthui = (SubstanceTableHeaderUI) ui;

			// synchronized (SubstanceTableUI.this.table) {
			int row = SubstanceTableUI.this.table.rowAtPoint(e.getPoint());
			int column = SubstanceTableUI.this.table
					.columnAtPoint(e.getPoint());
			if ((row < 0) || (row >= SubstanceTableUI.this.table.getRowCount())
					|| (column < 0)
					|| (column >= SubstanceTableUI.this.table.getColumnCount())) {
				this.fadeOutTableHeader();
				// System.out.println("Nulling RO column index");
				SubstanceTableUI.this.rolledOverColumn = -1;
			} else {
				// check if this is the same column
				if (SubstanceTableUI.this.rolledOverColumn == column)
					return;

				this.fadeOutTableHeader();
				FadeTracker.getInstance().trackFadeIn(FadeKind.ROLLOVER,
						header, column, false, sthui.getCallback(column));
				// System.out.println("Setting RO column index to " +
				// column);
				if (FadeConfigurationManager.getInstance().fadeAllowed(
						FadeKind.ROLLOVER, SubstanceTableUI.this.table)) {
					SubstanceTableUI.this.rolledOverColumn = column;
				}
			}
			// }
		}

		/**
		 * Initiates the fade out effect.
		 */
		private void fadeOut() {
			if (SubstanceTableUI.this.rolledOverId != null) {
				FadeTrackerCallback callback = SubstanceTableUI.this
						.getCallback(SubstanceTableUI.this.rolledOverId);
				if (SubstanceTableUI.this._hasRolloverAnimations()) {
					FadeTracker.getInstance()
							.trackFadeOut(FadeKind.ROLLOVER,
									SubstanceTableUI.this.table,
									SubstanceTableUI.this.rolledOverId, false,
									callback);
				} else {
					callback.fadeEnded(FadeKind.ROLLOVER);
				}
			}
		}

		/**
		 * Initiates the fade out effect.
		 */
		private void fadeOutTableHeader() {
			if (SubstanceTableUI.this.rolledOverColumn >= 0) {
				JTableHeader header = SubstanceTableUI.this.table
						.getTableHeader();
				if ((header == null) || (!header.isVisible()))
					return;
				SubstanceTableHeaderUI ui = (SubstanceTableHeaderUI) header
						.getUI();
				FadeTracker.getInstance().trackFadeOut(FadeKind.ROLLOVER,
						header, SubstanceTableUI.this.rolledOverColumn, false,
						ui.getCallback(SubstanceTableUI.this.rolledOverColumn));
			}
		}

	}

	/**
	 * Returns fade callback for a cell at the specified row and column.
	 * 
	 * @param row
	 *            Row index.
	 * @param column
	 *            Column index.
	 * @return Fade callback for the specified cell.
	 */
	private FadeTrackerCallback getCallback(int row, int column) {
		boolean hasRowSelection = this.table.getRowSelectionAllowed();
		boolean hasColumnSelection = this.table.getColumnSelectionAllowed();

		if (hasRowSelection && !hasColumnSelection)
			return new RowRepaintCallback(this.table, row);
		if (!hasRowSelection && hasColumnSelection)
			return new ColumnRepaintCallback(this.table, column);
		return new CellRepaintCallback(this.table, row, column);
	}

	/**
	 * Returns fade callback for a cell, row or column specified by the
	 * parameter, which should be one of {@link TableRowId},
	 * {@link TableColumnId} or {@link TableCellId}.
	 * 
	 * @param comparable
	 *            One of {@link TableRowId}, {@link TableColumnId} or
	 *            {@link TableCellId}.
	 * @return Fade callback.
	 */
	private FadeTrackerCallback getCallback(Comparable<?> comparable) {
		if (comparable instanceof TableRowId)
			return new RowRepaintCallback(this.table,
					((TableRowId) comparable).row);
		if (comparable instanceof TableColumnId)
			return new ColumnRepaintCallback(this.table,
					((TableColumnId) comparable).column);
		return new CellRepaintCallback(this.table,
				((TableCellId) comparable).row,
				((TableCellId) comparable).column);
	}

	/**
	 * Returns a comparable ID for the specified location. The result will be
	 * one of {@link TableRowId}, {@link TableColumnId} or {@link TableCellId},
	 * based on the row and column selection modes of the table.
	 * 
	 * @param row
	 *            Row index.
	 * @param column
	 *            Column index.
	 * @return Comparable ID for the specified location.
	 */
	public TableId getId(int row, int column) {
		boolean hasRowSelection = this.table.getRowSelectionAllowed();
		boolean hasColumnSelection = this.table.getColumnSelectionAllowed();

		// reuse the ID objects for optimizations - called a lot during
		// painting and animations
		if (hasRowSelection && !hasColumnSelection) {
			rowId.row = row;
			return rowId;
		}
		if (!hasRowSelection && hasColumnSelection) {
			colId.column = column;
			return colId;
		}
		cellId.column = column;
		cellId.row = row;
		return cellId;
	}

	TableRowId rowId;

	TableColumnId colId;

	TableCellId cellId;

	/**
	 * Synchronizes the current selection state.
	 * 
	 * @param e
	 *            Selection event.
	 */
	// @SuppressWarnings("unchecked")
	protected void syncSelection(/* ListSelectionEvent e */) {
		if (this.table == null) {
			// fix for defect 270 - if the UI delegate is updated
			// by another selection listener, ignore this
			return;
		}

		int rows = this.table.getRowCount();
		int cols = this.table.getColumnCount();

		int rowLeadIndex = this.table.getSelectionModel()
				.getLeadSelectionIndex();
		int colLeadIndex = this.table.getColumnModel().getSelectionModel()
				.getLeadSelectionIndex();
		boolean isFocusOwner = this.table.isFocusOwner();

		// fix for defect 209 - selection very slow on large tables with
		// column selection set to true and row selection set to false.
		// Solution - no selection animations on tables with more than 1000
		// cells.
		if (!this._hasSelectionAnimations()) {
			this.prevStateMap.clear();
			table.repaint();

			// fix for issue 414 - track focus on tables
			// without selection animations
			if (isFocusOwner) {
				this.focusedRow = rowLeadIndex;
				this.focusedColumn = colLeadIndex;
			}
			return;
		}

		Set<Long> initiatedFadeSequences = new HashSet<Long>();
		boolean fadeCanceled = false;

		FadeTracker fadeTrackerInstance = FadeTracker.getInstance();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (this.table.isCellSelected(i, j)) {
					TableCellId cellId = new TableCellId(i, j);
					// check if was selected before
					if (!this.selectedIndices.containsKey(cellId)) {
						// start fading in
						// System.err.println("Fade in on " + i + ":" + j);
						if (!fadeCanceled) {
							long fadeId = fadeTrackerInstance.trackFadeIn(
									FadeKind.SELECTION, this.table, cellId,
									false, new CellRepaintCallback(this.table,
											i, j));
							initiatedFadeSequences.add(fadeId);
							if (initiatedFadeSequences.size() > 15) {
								SubstanceFadeUtilities
										.cancelFades(initiatedFadeSequences);
								initiatedFadeSequences.clear();
								fadeCanceled = true;
							}
						} else {
							new CellRepaintCallback(this.table, i, j)
									.fadeEnded(FadeKind.SELECTION);
						}

						this.selectedIndices.put(cellId, this.table.getValueAt(
								i, j));
						// prevStateMap.put(cellId, ComponentState.SELECTED);
					}
				} else {
					TableCellId cellId = new TableCellId(i, j);
					// check if was selected before and still points
					// to the same element
					if (this.selectedIndices.containsKey(cellId)) {
						// corner case when the model returns null
						Object oldValue = this.selectedIndices.get(cellId);
						Object currValue = this.table.getValueAt(i, j);
						boolean isSame = false;
						if (oldValue == null) {
							isSame = (currValue == null);
						} else {
							// if (oldValue instanceof Comparable) {
							// try {
							// isSame = (((Comparable) oldValue)
							// .compareTo(currValue) == 0);
							// } catch (Throwable t) {
							// isSame = oldValue.toString().equals(
							// currValue.toString());
							// }
							// } else {
							isSame = oldValue.equals(currValue);
							// }
						}
						// if (!isSame) {
						// System.err.println(i + ":" + j + ":" + oldValue
						// + ":" + currValue);
						// }
						if (isSame) {
							// start fading out
							// System.err.println("Fade out on " + i + ":" + j);
							if (!fadeCanceled) {
								long fadeId = fadeTrackerInstance.trackFadeOut(
										FadeKind.SELECTION, this.table, cellId,
										false, new CellRepaintCallback(
												this.table, i, j));
								initiatedFadeSequences.add(fadeId);
								// System.err.println("Has "
								// + initiatedFadeSequences.size()
								// + " sqs [" + fadeId + "]");
								if (initiatedFadeSequences.size() > 15) {
									// System.err.println("Cancelling fades");
									SubstanceFadeUtilities
											.cancelFades(initiatedFadeSequences);
									initiatedFadeSequences.clear();
									fadeCanceled = true;
								}
							} else {
								new CellRepaintCallback(this.table, i, j)
										.fadeEnded(FadeKind.SELECTION);
							}
						}
						this.selectedIndices.remove(cellId);
					}
					// ComponentState state = getCellState(cellId);
					// if (state == ComponentState.DEFAULT) {
					// prevStateMap.remove(cellId);
					// } else {
					// prevStateMap.put(cellId, getCellState(cellId));
					// System.out.println(cellId.row + ":" + cellId.column
					// + "->" + state.name());
					// }
				}

				// handle focus animations
				boolean cellHasFocus = isFocusOwner && (i == rowLeadIndex)
						&& (j == colLeadIndex);
				if (cellHasFocus) {
					// check is it's a different cell
					if ((this.focusedRow != i) || (this.focusedColumn != j)) {
						if ((this.focusedRow >= 0) && (this.focusedColumn >= 0)) {
							// fade out the previous focus holder
							TableCellId prevFocusedId = new TableCellId(
									this.focusedRow, this.focusedColumn);
							// set indication to make exact comparison (since
							// focus can be only on one cell).
							prevFocusedId.setExactComparison(true);
							FadeTrackerCallback callback = this
									.getCallback(prevFocusedId);
							// System.out.println("Fading out " +
							// prevFocusedId);
							FadeTracker.getInstance().trackFadeOut(
									FadeKind.FOCUS, this.table, prevFocusedId,
									false, callback);
						}

						FadeTrackerCallback callback = this.getCallback(i, j);
						// fade in the current cell (new focus holder)
						// System.out.println("Fading in " + cellId);
						TableCellId currId = new TableCellId(i, j);
						// set indication to make exact comparison (since
						// focus can be only on one cell).
						currId.setExactComparison(true);
						FadeTracker.getInstance().trackFadeIn(FadeKind.FOCUS,
								this.table, currId, false, callback);
						// System.out.println("Setting focus index to " + i +
						// ":"
						// + j);
						if (FadeConfigurationManager.getInstance().fadeAllowed(
								FadeKind.FOCUS, this.table)) {
							// and store it for future checks
							this.focusedRow = i;
							this.focusedColumn = j;
						}
					}
				} else {
					// check if previously it held focus
					if ((this.focusedRow == i) && (this.focusedColumn == j)) {
						// fade it out
						TableCellId prevFocusedId = new TableCellId(
								this.focusedRow, this.focusedColumn);
						// set indication to make exact comparison (since
						// focus can be only on one cell).
						prevFocusedId.setExactComparison(true);
						FadeTrackerCallback callback = SubstanceTableUI.this
								.getCallback(prevFocusedId);
						// System.out.println("Fading out " + prevFocusedId);
						FadeTracker.getInstance().trackFadeOut(FadeKind.FOCUS,
								SubstanceTableUI.this.table, prevFocusedId,
								false, callback);
						this.focusedRow = -1;
						this.focusedColumn = -1;
					}
				}
			}
		}
		// System.err.println("Has " + currSelected.size() + " selected cells");
		// for (TableCellId cellId : currSelected.keySet()) {
		// System.err.println("\t" + cellId.row + ":" + cellId.column);
		// }
	}

	/**
	 * Returns the previous state for the specified cell.
	 * 
	 * @param cellIndex
	 *            Cell index.
	 * @return The previous state for the specified cell.
	 */
	public ComponentState getPrevCellState(TableCellId cellIndex) {
		if (this.prevStateMap.containsKey(cellIndex))
			return this.prevStateMap.get(cellIndex);
		return this.getCellState(cellIndex);
	}

	/**
	 * Returns the current state for the specified cell.
	 * 
	 * @param cellIndex
	 *            Cell index.
	 * @return The current state for the specified cell.
	 */
	public ComponentState getCellState(TableCellId cellIndex) {
		int row = cellIndex.row;
		int column = cellIndex.column;
		Comparable<?> cellId = this.getId(row, column);
		boolean isEnabled = this.table.isEnabled();
		boolean isRollover = cellId.equals(this.rolledOverId);
		boolean isSelected = false;
		boolean hasSelectionAnimations = (this.updateInfo != null) ? this.updateInfo.hasSelectionAnimations
				: this._hasSelectionAnimations();
		if (hasSelectionAnimations
				&& FadeConfigurationManager.getInstance().fadeAllowed(
						FadeKind.SELECTION, table))
			isSelected = this.selectedIndices.containsKey(cellId);
		else {
			isSelected = this.table.isCellSelected(row, column);
		}

		return ComponentState.getState(isEnabled, isRollover, isSelected);
	}

	/**
	 * Checks whether the table has animations.
	 * 
	 * @return <code>true</code> if the table has animations, <code>false</code>
	 *         otherwise.
	 */
	protected boolean _hasAnimations() {
		// fix for defects 164 and 209 - selection
		// and deletion are very slow on large tables.
		int rowCount = this.table.getRowCount();
		int colCount = this.table.getColumnCount();
		if (rowCount * colCount >= 500)
			return false;
		if (this.table.getColumnSelectionAllowed()
				&& !this.table.getRowSelectionAllowed()) {
			if (!this.table.getShowHorizontalLines()
					&& !this.table.getShowVerticalLines())
				return rowCount <= 8;
			return rowCount <= 25;
		}
		if (!this.table.getColumnSelectionAllowed()
				&& this.table.getRowSelectionAllowed()) {
			if (!this.table.getShowHorizontalLines()
					&& !this.table.getShowVerticalLines())
				return colCount <= 8;
			return colCount <= 25;
		}
		return true;
	}

	/**
	 * Checks whether the table has selection animations.
	 * 
	 * @return <code>true</code> if the table has selection animations,
	 *         <code>false</code> otherwise.
	 */
	protected boolean _hasSelectionAnimations() {
		return this._hasAnimations()
				&& !LafWidgetUtilities.hasNoFades(this.table,
						FadeKind.SELECTION);
	}

	/**
	 * Checks whether the table has rollover animations.
	 * 
	 * @return <code>true</code> if the table has rollover animations,
	 *         <code>false</code> otherwise.
	 */
	protected boolean _hasRolloverAnimations() {
		return this._hasAnimations()
				&& !LafWidgetUtilities
						.hasNoFades(this.table, FadeKind.ROLLOVER);
	}

	/**
	 * Returns the index of the rollover column.
	 * 
	 * @return The index of the rollover column.
	 */
	public int getRolloverColumnIndex() {
		return this.rolledOverColumn;
	}

	/**
	 * Returns indication whether the specified cell has focus.
	 * 
	 * @param row
	 *            Cell row index.
	 * @param column
	 *            Cell column index.
	 * @return <code>true</code> If the focus is on the specified cell,
	 *         <code>false</code> otherwise.
	 */
	public boolean isFocusedCell(int row, int column) {
		return (this.focusedRow == row) && (this.focusedColumn == column);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void update(Graphics g, JComponent c) {
		BackgroundPaintingUtils.updateIfOpaque(g, c);
		Graphics2D g2d = (Graphics2D) g.create();
		SubstanceStripingUtils.setup(c);
		this.updateInfo = new UpdateOptimizationInfo();
		this.paint(g2d, c);
		SubstanceStripingUtils.tearDown(c);
		g2d.dispose();
		this.updateInfo = null;
	}

	/**
	 * Returns the cell renderer insets of this table. Is for internal use only.
	 * 
	 * @return The cell renderer insets of this table.
	 */
	public Insets getCellRendererInsets() {
		return this.cellRendererInsets;
	}

	public SubstanceColorScheme getDefaultColorScheme() {
		if (this.updateInfo != null)
			return this.updateInfo.defaultScheme;
		return null;
	}

	public SubstanceColorScheme getHighlightColorScheme(ComponentState state) {
		if (this.updateInfo != null)
			return updateInfo.getHighlightColorScheme(state);
		return null;
	}

	public boolean hasSelectionAnimations() {
		if (this.updateInfo != null)
			return this.updateInfo.hasSelectionAnimations;
		return this._hasSelectionAnimations();
	}

	public boolean hasRolloverAnimations() {
		if (this.updateInfo != null)
			return this.updateInfo.hasRolloverAnimations;
		return this._hasRolloverAnimations();
	}

	private UpdateOptimizationInfo updateInfo;

	private class UpdateOptimizationInfo {
		public boolean toDrawWatermark;

		private Map<ComponentState, SubstanceColorScheme> highlightSchemeMap;

		private Map<ComponentState, SubstanceColorScheme> borderSchemeMap;

		private Map<ComponentState, Float> highlightAlphaMap;

		public SubstanceColorScheme defaultScheme;

		public boolean hasSelectionAnimations;

		public boolean hasRolloverAnimations;

		public Map<Component, Boolean> opacity;

		public DecorationAreaType decorationAreaType;

		public boolean isInDecorationArea;

		public UpdateOptimizationInfo() {
			this.toDrawWatermark = SubstanceCoreUtilities
					.toDrawWatermark(table);
			this.defaultScheme = SubstanceColorSchemeUtilities.getColorScheme(
					table, ComponentState.DEFAULT);
			this.highlightAlphaMap = new EnumMap<ComponentState, Float>(
					ComponentState.class);
			this.highlightSchemeMap = new EnumMap<ComponentState, SubstanceColorScheme>(
					ComponentState.class);
			this.borderSchemeMap = new EnumMap<ComponentState, SubstanceColorScheme>(
					ComponentState.class);
			this.hasSelectionAnimations = _hasSelectionAnimations();
			this.opacity = new HashMap<Component, Boolean>();
			this.decorationAreaType = SubstanceLookAndFeel
					.getDecorationType(table);

			SubstanceSkin skin = SubstanceCoreUtilities.getSkin(table);
			this.isInDecorationArea = (this.decorationAreaType != null)
					&& skin
							.isRegisteredAsDecorationArea(this.decorationAreaType)
					&& TransitionLayout.isOpaque(table);
		}

		public SubstanceColorScheme getHighlightColorScheme(ComponentState state) {
			if (!this.highlightSchemeMap.containsKey(state)) {
				this.highlightSchemeMap.put(state,
						SubstanceColorSchemeUtilities.getColorScheme(table,
								ColorSchemeAssociationKind.HIGHLIGHT, state));
			}
			return this.highlightSchemeMap.get(state);
		}

		public SubstanceColorScheme getHighlightBorderColorScheme(
				ComponentState state) {
			if (!this.borderSchemeMap.containsKey(state)) {
				this.borderSchemeMap.put(state, SubstanceColorSchemeUtilities
						.getColorScheme(table,
								ColorSchemeAssociationKind.HIGHLIGHT_BORDER,
								state));
			}
			return this.borderSchemeMap.get(state);
		}

		public float getHighlightAlpha(ComponentState state) {
			if (!this.highlightAlphaMap.containsKey(state)) {
				this.highlightAlphaMap.put(state, SubstanceColorSchemeUtilities
						.getHighlightAlpha(table, state));
			}
			return this.highlightAlphaMap.get(state);
		}
	}

	private boolean isSubstanceDefaultRenderer(TableCellRenderer renderer) {
		return (renderer instanceof SubstanceDefaultTableCellRenderer)
				|| (renderer instanceof SubstanceDefaultTableCellRenderer.BooleanRenderer);
	}
}
