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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.*;

import org.jvnet.lafwidget.animation.*;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.api.*;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableHeaderCellRenderer;
import org.jvnet.substance.painter.utils.HighlightPainterUtils;
import org.jvnet.substance.utils.*;

/**
 * UI for table headers in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceTableHeaderUI extends BasicTableHeaderUI {
	/**
	 * Repaints the header on column selection.
	 */
	protected TableHeaderListener substanceHeaderListener;

	/**
	 * The default renderer.
	 */
	protected TableCellRenderer defaultHeaderRenderer;

	/**
	 * Holds the list of currently selected indices.
	 */
	protected Map<Integer, Object> selectedIndices;

	/**
	 * Listener for fade animations on list selections.
	 */
	protected ListSelectionListener substanceFadeSelectionListener;

	/**
	 * Map of previous fade states (for state-aware color scheme transitions).
	 */
	private Map<Integer, ComponentState> prevStateMap;

	/**
	 * Map of next fade states (for state-aware color scheme transitions).
	 */
	private Map<Integer, ComponentState> nextStateMap;

	/**
	 * Property change listener.
	 */
	protected PropertyChangeListener substancePropertyChangeListener;

	/**
	 * Listener for table header.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private static class TableHeaderListener implements ListSelectionListener {
		/**
		 * The associated table header UI.
		 */
		private SubstanceTableHeaderUI ui;

		/**
		 * Simple constructor.
		 * 
		 * @param ui
		 *            The associated table header UI
		 */
		public TableHeaderListener(SubstanceTableHeaderUI ui) {
			this.ui = ui;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.
		 * event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			if (ui.header == null)
				return;
			if (ui.header.isValid())
				ui.header.repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceTableHeaderUI();
	}

	/**
	 * Creates a new UI delegate.
	 */
	public SubstanceTableHeaderUI() {
		prevStateMap = new HashMap<Integer, ComponentState>();
		nextStateMap = new HashMap<Integer, ComponentState>();
		selectedIndices = new HashMap<Integer, Object>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTableHeaderUI#installListeners()
	 */
	@Override
	protected void installListeners() {
		super.installListeners();
		TableColumnModel columnModel = header.getColumnModel();
		if (columnModel != null) {
			ListSelectionModel lsm = columnModel.getSelectionModel();
			if (lsm != null) {
				// fix for defect 109 - memory leak on skin switch
				substanceHeaderListener = new TableHeaderListener(this);
				lsm.addListSelectionListener(substanceHeaderListener);
			}
		}

		// Add listener for the selection animation
		this.substanceFadeSelectionListener = new ListSelectionListener() {
			protected void cancelFades(Set<Long> initiatedFadeSequences) {
				FadeTracker fadeTrackerInstance = FadeTracker.getInstance();
				for (long fadeId : initiatedFadeSequences) {
					fadeTrackerInstance.cancelFadeInstance(fadeId);
				}
			}

			public void valueChanged(ListSelectionEvent e) {
				if (header == null)
					return;

				// fix for issue 367 - check that there is an associated
				// table and that it has a substance UI delegate
				JTable table = header.getTable();
				if (table == null)
					return;

				TableUI ui = table.getUI();
				if (!(ui instanceof SubstanceTableUI))
					return;

				// optimization on large tables and large selections
				// and syncing the fade presence with the table
				// (issue 309)
				SubstanceTableUI tableUI = (SubstanceTableUI) ui;
				// System.out.println("Sel anim: "
				// + tableUI.hasSelectionAnimations());
				if (!tableUI._hasSelectionAnimations())
					return;

				Set<Long> initiatedFadeSequences = new HashSet<Long>();
				boolean fadeCanceled = false;

				// if (SubstanceCoreUtilities.toBleedWatermark(list))
				// return;

				FadeTracker fadeTrackerInstance = FadeTracker.getInstance();
				TableColumnModel columnModel = header.getColumnModel();
				int size = columnModel.getColumnCount();
				ListSelectionModel lsm = columnModel.getSelectionModel();
				for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
					if (i >= size)
						continue;
					if (lsm.isSelectedIndex(i)) {
						// check if was selected before
						if (!selectedIndices.containsKey(i)) {
							// start fading in
							// System.out.println("Fade in on index " + i);

							if (!fadeCanceled) {
								long fadeId = fadeTrackerInstance.trackFadeIn(
										FadeKind.SELECTION, header, i, false,
										new ColumnHeaderRepaintCallback(header,
												i));
								initiatedFadeSequences.add(fadeId);
								if (initiatedFadeSequences.size() > 25) {
									cancelFades(initiatedFadeSequences);
									initiatedFadeSequences.clear();
									fadeCanceled = true;
								}
							}

							selectedIndices.put(i, columnModel.getColumn(i));
						}
					} else {
						// check if was selected before and still points to the
						// same element
						if (selectedIndices.containsKey(i)) {
							if (selectedIndices.get(i) == columnModel
									.getColumn(i)) {
								// start fading out
								// System.out.println("Fade out on index " + i);

								if (!fadeCanceled) {
									long fadeId = fadeTrackerInstance
											.trackFadeOut(
													FadeKind.SELECTION,
													header,
													i,
													false,
													new ColumnHeaderRepaintCallback(
															header, i));
									initiatedFadeSequences.add(fadeId);
									if (initiatedFadeSequences.size() > 25) {
										cancelFades(initiatedFadeSequences);
										initiatedFadeSequences.clear();
										fadeCanceled = true;
									}
								}
							}
							selectedIndices.remove(i);
						}
					}
				}
			}
		};

		if (columnModel != null) {
			ListSelectionModel lsm = columnModel.getSelectionModel();
			if (lsm != null) {
				lsm.addListSelectionListener(substanceFadeSelectionListener);
			}
		}

		this.substancePropertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("table".equals(evt.getPropertyName())) {
					// track changes to the table and re-register the
					// column model listener to the new table.
					TableColumnModel oldModel = (evt.getOldValue() instanceof JTable) ? ((JTable) evt
							.getOldValue()).getColumnModel()
							: null;
					TableColumnModel newModel = (evt.getNewValue() instanceof JTable) ? ((JTable) evt
							.getNewValue()).getColumnModel()
							: null;
					processColumnModelChangeEvent(oldModel, newModel);
				}
			}
		};
		this.header
				.addPropertyChangeListener(this.substancePropertyChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTableHeaderUI#installDefaults()
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();

		defaultHeaderRenderer = header.getDefaultRenderer();
		if (defaultHeaderRenderer instanceof UIResource) {
			header
					.setDefaultRenderer(new SubstanceDefaultTableHeaderCellRenderer());
		}

		for (int i = 0; i < header.getColumnModel().getColumnCount(); i++) {
			if (header.getColumnModel().getSelectionModel().isSelectedIndex(i)) {
				selectedIndices.put(i, header.getColumnModel().getColumn(i));
				prevStateMap.put(i, ComponentState.SELECTED);
			}
		}
		this.header.putClientProperty(SubstanceCoreUtilities.USE_HIGHLIGHT,
				Boolean.TRUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTableHeaderUI#uninstallListeners()
	 */
	@Override
	protected void uninstallListeners() {
		// fix for defect 109 - memory leak on skin switch
		TableColumnModel columnModel = header.getColumnModel();
		if (columnModel != null) {
			ListSelectionModel lsm = columnModel.getSelectionModel();
			if (lsm != null) {
				lsm.removeListSelectionListener(substanceHeaderListener);
				substanceHeaderListener = null;
			}
		}

		this.header
				.removePropertyChangeListener(this.substancePropertyChangeListener);
		this.substancePropertyChangeListener = null;

		super.uninstallListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTableHeaderUI#uninstallDefaults()
	 */
	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		selectedIndices.clear();

		if (header.getDefaultRenderer() instanceof SubstanceDefaultTableHeaderCellRenderer) {
			header.setDefaultRenderer(defaultHeaderRenderer);
			if (defaultHeaderRenderer instanceof Component)
				SwingUtilities
						.updateComponentTreeUI((Component) defaultHeaderRenderer);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#paint(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		if (header.getColumnModel().getColumnCount() <= 0) {
			return;
		}
		boolean ltr = header.getComponentOrientation().isLeftToRight();

		Rectangle clip = g.getClipBounds();
		Point left = clip.getLocation();
		Point right = new Point(clip.x + clip.width - 1, clip.y);

		TableColumnModel cm = header.getColumnModel();
		int[] selectedColumns = cm.getSelectedColumns();
		Set<Integer> selected = new HashSet<Integer>();
		for (int sel : selectedColumns)
			selected.add(sel);

		int cMin = header.columnAtPoint(ltr ? left : right);
		int cMax = header.columnAtPoint(ltr ? right : left);
		// This should never happen.
		if (cMin == -1) {
			cMin = 0;
		}
		// If the table does not have enough columns to fill the view we'll get
		// -1.
		// Replace this with the index of the last column.
		if (cMax == -1) {
			cMax = cm.getColumnCount() - 1;
		}

		TableColumn draggedColumn = header.getDraggedColumn();
		int columnWidth;
		Rectangle cellRect = header.getHeaderRect(ltr ? cMin : cMax);
		TableColumn aColumn;
		if (ltr) {
			for (int column = cMin; column <= cMax; column++) {
				aColumn = cm.getColumn(column);
				columnWidth = aColumn.getWidth();
				cellRect.width = columnWidth;
				if (aColumn != draggedColumn) {
					this.paintCell(g, cellRect, column, selected
							.contains(column));
				}
				cellRect.x += columnWidth;
			}
		} else {
			for (int column = cMax; column >= cMin; column--) {
				aColumn = cm.getColumn(column);
				columnWidth = aColumn.getWidth();
				cellRect.width = columnWidth;
				if (aColumn != draggedColumn) {
					this.paintCell(g, cellRect, column, selected
							.contains(column));
				}
				cellRect.x += columnWidth;
			}
		}

		this.paintGrid(g, c);

		// Paint the dragged column if we are dragging.
		if (draggedColumn != null) {
			int draggedColumnIndex = viewIndexForColumn(draggedColumn);
			Rectangle draggedCellRect = header
					.getHeaderRect(draggedColumnIndex);

			// Draw a gray well in place of the moving column.
			g.setColor(header.getParent().getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
					draggedCellRect.width, draggedCellRect.height);

			draggedCellRect.x += header.getDraggedDistance();

			// Fill the background.
			g.setColor(header.getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
					draggedCellRect.width, draggedCellRect.height);

			this.paintCell(g, draggedCellRect, draggedColumnIndex, selected
					.contains(draggedColumnIndex));
		}

		// Remove all components in the rendererPane.
		rendererPane.removeAll();
	}

	/**
	 * Retrieves renderer for the specified column header.
	 * 
	 * @param columnIndex
	 *            Column index.
	 * @return Renderer for the specified column header.
	 */
	private Component getHeaderRenderer(int columnIndex) {
		TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
		TableCellRenderer renderer = aColumn.getHeaderRenderer();
		if (renderer == null) {
			renderer = header.getDefaultRenderer();
		}
		return renderer.getTableCellRendererComponent(header.getTable(),
				aColumn.getHeaderValue(), false, false, -1, columnIndex);
	}

	/**
	 * 
	 * 
	 * @param g
	 * @param c
	 */
	protected void paintGrid(Graphics g, JComponent c) {
		boolean ltr = header.getComponentOrientation().isLeftToRight();

		Graphics2D g2d = (Graphics2D) g.create();

		Rectangle clip = g.getClipBounds();
		Point left = clip.getLocation();
		// tweak the points for issue 378 - making sure that the
		// grid lines are repainted correctly on scroll.
		int lineWeight = (int) Math.ceil(SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(c)));
		left = new Point(left.x - 2 * lineWeight, left.y);
		Point right = new Point(clip.x + clip.width + 2 * lineWeight, clip.y);

		TableColumnModel cm = header.getColumnModel();

		int cMin = header.columnAtPoint(ltr ? left : right);
		int cMax = header.columnAtPoint(ltr ? right : left);
		// This should never happen.
		if (cMin == -1) {
			cMin = 0;
		}

		Rectangle cellRect0 = header.getHeaderRect(cMin);
		// int top = cellRect0.y;
		int bottom = cellRect0.y + cellRect0.height;

		Color gridColor = getGridColor(this.header);

		float strokeWidth = SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(this.header));
		g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(gridColor);
		g2d.setComposite(TransitionLayout.getAlphaComposite(this.header, 0.7f,
				g));

		// g2d.setColor(Color.red);
		g2d.drawLine((int) left.getX(), (int) (bottom - strokeWidth / 2),
				(int) right.getX(), (int) (bottom - strokeWidth / 2));
		// g2d.setColor(gridColor);
		// g2d.drawLine((int) left.getX(), top + (int) (strokeWidth / 2),
		// (int) right.getX(), top + (int) (strokeWidth / 2));

		// If the table does not have enough columns to fill the view we'll
		// get
		// -1.
		// Replace this with the index of the last column.
		if (cMax == -1) {
			cMax = cm.getColumnCount() - 1;
		}

		TableColumn draggedColumn = header.getDraggedColumn();
		int columnWidth;
		Rectangle cellRect = header.getHeaderRect(ltr ? cMin : cMax);
		TableColumn aColumn;
		if (ltr) {
			for (int column = cMin; column <= cMax; column++) {
				aColumn = cm.getColumn(column);
				columnWidth = aColumn.getWidth();
				cellRect.width = columnWidth;
				if (aColumn != draggedColumn) {
					g2d.drawLine(cellRect.x - 1, cellRect.y, cellRect.x - 1,
							bottom);
					// if (column == cMax) {
					// g2d.drawLine(cellRect.x + cellRect.width - 1,
					// cellRect.y, cellRect.x + cellRect.width - 1,
					// bottom);
					// }
				}
				cellRect.x += columnWidth;
			}
		} else {
			for (int column = cMax; column >= cMin; column--) {
				aColumn = cm.getColumn(column);
				columnWidth = aColumn.getWidth();
				cellRect.width = columnWidth;
				if (aColumn != draggedColumn) {
					g2d.drawLine(cellRect.x - 1, cellRect.y, cellRect.x - 1,
							bottom);
					// if (column == cMin) {
					// g2d.drawLine(cellRect.x + cellRect.width - 1,
					// cellRect.y, cellRect.x + cellRect.width - 1,
					// bottom);
					// }
				}
				cellRect.x += columnWidth;
			}
		}

		g2d.dispose();
	}

	/**
	 * Returns the grid color for the table header.
	 * 
	 * @param header
	 *            Table header.
	 * @return Grid color.
	 */
	protected static Color getGridColor(JTableHeader header) {
		ComponentState currState = (header.isEnabled() && header.getTable()
				.isEnabled()) ? ComponentState.DEFAULT
				: ComponentState.DISABLED_UNSELECTED;
		Color gridColor = SubstanceColorSchemeUtilities.getColorScheme(header,
				ColorSchemeAssociationKind.BORDER, currState).getLineColor();
		return gridColor;
	}

	/**
	 * Paints cell.
	 * 
	 * @param g
	 *            Graphic context.
	 * @param cellRect
	 *            Cell rectangle.
	 * @param columnIndex
	 *            Column index.
	 * @param isSelected
	 *            Selection indication.
	 */
	private void paintCell(Graphics g, Rectangle cellRect, int columnIndex,
			boolean isSelected) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(TransitionLayout.getAlphaComposite(header, g));

		// paint default background
		Component component = getHeaderRenderer(columnIndex);
		ComponentState prevState = getPrevColumnState(columnIndex);
		ComponentState currState = getColumnState(columnIndex);

		// Compute the alpha values for the animation.
		float startAlpha = SubstanceColorSchemeUtilities.getHighlightAlpha(
				header, prevState);
		float endAlpha = SubstanceColorSchemeUtilities.getHighlightAlpha(
				header, currState);

		FadeState state = SubstanceFadeUtilities.getFadeState(header,
				columnIndex, FadeKind.SELECTION, FadeKind.ROLLOVER);
		float totalHighlightAlpha = endAlpha;
		float fadeCoef = 0.0f;
		if (state != null) {
			fadeCoef = state.getFadePosition();

			// compute the total alpha of the overlays.
			if (state.isFadingIn()) {
				totalHighlightAlpha = startAlpha + (endAlpha - startAlpha)
						* fadeCoef;
			} else {
				totalHighlightAlpha = startAlpha + (endAlpha - startAlpha)
						* (1.0f - fadeCoef);
			}

			if (state.isFadingIn())
				fadeCoef = 1.0f - fadeCoef;
		}

		if (totalHighlightAlpha > 0.0f) {
			g2d.setComposite(TransitionLayout.getAlphaComposite(this.header,
					totalHighlightAlpha, g));
			HighlightPainterUtils.paintHighlight(g2d, rendererPane, component, cellRect,
					0.0f, null, currState, prevState, fadeCoef);
			g2d
					.setComposite(TransitionLayout.getAlphaComposite(
							this.header, g));
		}

		rendererPane.paintComponent(g2d, component, header, cellRect.x,
				cellRect.y, cellRect.width, cellRect.height, true);

		g2d.dispose();
	}

	/**
	 * Retrieves view index for the specified column.
	 * 
	 * @param aColumn
	 *            Table column.
	 * @return View index for the specified column.
	 */
	private int viewIndexForColumn(TableColumn aColumn) {
		TableColumnModel cm = header.getColumnModel();
		for (int column = 0; column < cm.getColumnCount(); column++) {
			if (cm.getColumn(column) == aColumn) {
				return column;
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void update(Graphics g, JComponent c) {
		// fix for issue 175 - table header under resize mode off
		// was painted in color scheme-agnostic (gray) color.
		ComponentState backgroundState = (this.header.isEnabled() && this.header
				.getTable().isEnabled()) ? ComponentState.DEFAULT
				: ComponentState.DISABLED_UNSELECTED;

		// fix for issue 360 - respect the clip bounds of the
		// table header
		Rectangle clip = g.getClipBounds();
		if (clip == null)
			clip = c.getBounds();

		// do not use the highlight scheme for painting the
		// table header background
		SubstanceColorScheme fillScheme = SubstanceColorSchemeUtilities
				.getColorScheme(c, backgroundState);
		SubstanceColorScheme borderScheme = SubstanceColorSchemeUtilities
				.getColorScheme(c, ColorSchemeAssociationKind.BORDER,
						backgroundState);

		HighlightPainterUtils.paintHighlight(g, null, c, clip, 0.0f, null,
				fillScheme, fillScheme, borderScheme, borderScheme, 0.0f);
		paint(g, c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicTableHeaderUI#uninstallUI(javax.swing.JComponent
	 * )
	 */
	@Override
	public void uninstallUI(JComponent c) {
		for (int i = 0; i < header.getColumnModel().getColumnCount(); i++) {
			TableColumn aColumn = header.getColumnModel().getColumn(i);
			TableCellRenderer renderer = aColumn.getHeaderRenderer();
			if (renderer == null) {
				renderer = header.getDefaultRenderer();
			}
			Component rendComp = renderer.getTableCellRendererComponent(header
					.getTable(), aColumn.getHeaderValue(), false, false, -1, i);
			SwingUtilities.updateComponentTreeUI(rendComp);
		}
		super.uninstallUI(c);
	}

	/**
	 * Returns the previous state for the specified column.
	 * 
	 * @param columnIndex
	 *            Column index.
	 * @return The previous state for the specified column.
	 */
	public ComponentState getPrevColumnState(int columnIndex) {
		if (this.header.isEnabled()
				&& this.header.getTable().isEnabled()
				&& SubstanceDefaultTableHeaderCellRenderer.isColumnSorted(
						this.header.getTable(), columnIndex))
			return ComponentState.SELECTED;

		if (prevStateMap.containsKey(columnIndex))
			return prevStateMap.get(columnIndex);
		return getColumnState(columnIndex);
	}

	/**
	 * Returns the current state for the specified cell.
	 * 
	 * @param columnIndex
	 *            Column index.
	 * @return The current state for the specified column.
	 */
	public ComponentState getColumnState(int columnIndex) {
		ButtonModel synthModel = new DefaultButtonModel();
		boolean toEnable = header.isEnabled();

		// get the rollover column index from the table UI delegate
		JTable table = this.header.getTable();
		if (table != null) {
			toEnable = toEnable && table.isEnabled();
			TableUI ui = table.getUI();
			if (ui instanceof SubstanceTableUI) {
				SubstanceTableUI tableUI = (SubstanceTableUI) table.getUI();
				int rolledOverIndex = tableUI.getRolloverColumnIndex();
				synthModel.setRollover((rolledOverIndex >= 0)
						&& (rolledOverIndex == columnIndex));
			}
		}

		if (toEnable
				&& SubstanceDefaultTableHeaderCellRenderer.isColumnSorted(
						this.header.getTable(), columnIndex))
			return ComponentState.SELECTED;

		synthModel.setEnabled(toEnable);

		// set selection on the model.
		TableColumnModel columnModel = header.getColumnModel();
		synthModel
				.setSelected(columnModel.getColumnSelectionAllowed()
						&& columnModel.getSelectionModel().isSelectedIndex(
								columnIndex));
		return ComponentState.getState(synthModel, null);
	}

	/**
	 * Returns the animation callback for the specified column.
	 * 
	 * @param columnIndex
	 *            Column index.
	 * @return Animation callback for the specified column.
	 */
	public FadeTrackerCallback getCallback(int columnIndex) {
		return new ColumnHeaderRepaintCallback(this.header, columnIndex);
	}

	/**
	 * Returns the scroll pane corner filler component. This method is used in
	 * {@link SubstanceScrollPaneUI} to put a consistent filler for tables.
	 * 
	 * @return Scroll pane corner filler.
	 */
	public JComponent getScrollPaneCornerFiller() {
		return new ScrollPaneCornerFiller(this.header);
	}

	/**
	 * Corner filler for tables wrapped in scroll panes.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected static class ScrollPaneCornerFiller extends JComponent implements
			UIResource {
		/**
		 * Associated table header.
		 */
		protected JTableHeader header;

		/**
		 * Creates a new corner filler.
		 * 
		 * @param header
		 *            Table header.
		 */
		public ScrollPaneCornerFiller(JTableHeader header) {
			this.header = header;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			// System.err.println("Painting " + this.hashCode() + " from "
			// + ((header == null) ? "null" : header.hashCode()));

			boolean ltr = header.getComponentOrientation().isLeftToRight();
			final ComponentState backgroundState = (header.isEnabled() && header
					.getTable().isEnabled()) ? ComponentState.DEFAULT
					: ComponentState.DISABLED_UNSELECTED;
			HighlightPainterUtils.paintHighlight(g2d, null, this.header,
					new Rectangle(0, 0, this.getWidth(), this.getHeight()),
					0.0f, null, backgroundState, backgroundState, 0.0f);

			g2d.setColor(getGridColor(this.header));
			float strokeWidth = SubstanceSizeUtils
					.getBorderStrokeWidth(SubstanceSizeUtils
							.getComponentFontSize(header));
			g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_BEVEL));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setComposite(TransitionLayout.getAlphaComposite(this.header,
					0.7f, g));

			int x = ltr ? (int) strokeWidth / 2 : getWidth() - 1
					- (int) strokeWidth / 2;
			g2d.drawLine(x, 0, x, getHeight());

			g2d.dispose();
		}
	}

	/**
	 * Processes the events on model changes on the table column model.
	 * 
	 * @param oldModel
	 *            Old column model.
	 * @param newModel
	 *            New column model.
	 */
	public void processColumnModelChangeEvent(TableColumnModel oldModel,
			TableColumnModel newModel) {
		if (oldModel != null) {
			oldModel.getSelectionModel().removeListSelectionListener(
					substanceFadeSelectionListener);
		}
		if (newModel != null) {
			newModel.getSelectionModel().addListSelectionListener(
					substanceFadeSelectionListener);
		}
		selectedIndices.clear();
		prevStateMap.clear();
		nextStateMap.clear();
	}

	/**
	 * Repaints a single column header during the fade animation cycle.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class ColumnHeaderRepaintCallback extends FadeTrackerAdapter {
		/**
		 * Associated table header.
		 */
		protected JTableHeader header;

		/**
		 * Associated (animated) column index.
		 */
		protected int columnIndex;

		/**
		 * Creates a new animation repaint callback.
		 * 
		 * @param header
		 *            Associated table header.
		 * @param columnIndex
		 *            Associated (animated) column index.
		 */
		public ColumnHeaderRepaintCallback(JTableHeader header, int columnIndex) {
			this.header = header;
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
			if ((SubstanceTableHeaderUI.this.header == header)
					&& (columnIndex < header.getColumnModel().getColumnCount())) {
				ComponentState currState = getColumnState(columnIndex);
				if (currState == ComponentState.DEFAULT) {
					prevStateMap.remove(columnIndex);
					nextStateMap.remove(columnIndex);
				} else {
					prevStateMap.put(columnIndex, currState);
					nextStateMap.put(columnIndex, currState);
				}
				// System.out.println(tabIndex + "->"
				// + prevStateMap.get(tabIndex).name());
			}
			repaintColumnHeader();
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
			if ((SubstanceTableHeaderUI.this.header == header)
					&& (columnIndex < header.getColumnModel().getColumnCount())) {
				ComponentState nextState = nextStateMap.get(columnIndex);
				if (nextState == null) {
					prevStateMap.remove(columnIndex);
				} else {
					prevStateMap.put(columnIndex, nextState);
				}
				// System.out.println(tabIndex + "->"
				// + prevStateMap.get(tabIndex).name());
			}
			repaintColumnHeader();
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
			if ((SubstanceTableHeaderUI.this.header == header)
					&& (columnIndex < header.getColumnModel().getColumnCount())) {
				nextStateMap.put(columnIndex, getColumnState(columnIndex));
			}
			repaintColumnHeader();
		}

		/**
		 * Repaints the associated cell.
		 */
		private void repaintColumnHeader() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (header == null) {
						// may happen if the LAF was switched in the meantime
						return;
					}
					try {
						// maybeUpdateLayoutState();
						int cellCount = header.getColumnModel()
								.getColumnCount();
						if ((cellCount > 0) && (columnIndex < cellCount)) {
							// need to retrieve the cell rectangle since the
							// cells can be moved while animating
							Rectangle rect = header.getHeaderRect(columnIndex);
							Rectangle damaged = new Rectangle(rect.x - 5,
									rect.y, rect.width + 10, rect.height);
							header.repaint(damaged);
						}
					} catch (RuntimeException re) {
						return;
					}
				}
			});
		}
	}
}
