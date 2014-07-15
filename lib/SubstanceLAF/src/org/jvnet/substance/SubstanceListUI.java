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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;

import org.jvnet.lafwidget.LafWidgetUtilities;
import org.jvnet.lafwidget.animation.*;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.api.*;
import org.jvnet.substance.api.renderers.SubstanceDefaultListCellRenderer;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.utils.BackgroundPaintingUtils;
import org.jvnet.substance.painter.utils.HighlightPainterUtils;
import org.jvnet.substance.utils.*;

/**
 * UI for lists in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceListUI extends BasicListUI {
	/**
	 * Holds the list of currently selected indices.
	 */
	protected Map<Integer, Object> selectedIndices;

	/**
	 * Holds the currently rolled-over index, or -1 is there is none such.
	 */
	protected int rolledOverIndex;

	/**
	 * Property listener that listens to the
	 * {@link SubstanceLookAndFeel#WATERMARK_TO_BLEED} property.
	 */
	protected PropertyChangeListener substancePropertyChangeListener;

	/**
	 * Local cache of JList's client property "List.isFileList"
	 */
	protected boolean isFileList;

	/**
	 * Local cache of JList's component orientation property
	 */
	protected boolean isLeftToRight;

	/**
	 * Listener for fade animations on list selections.
	 */
	protected ListSelectionListener substanceFadeSelectionListener;

	/**
	 * Listener for fade animations on list rollovers.
	 */
	protected RolloverFadeListener substanceFadeRolloverListener;

	/**
	 * Map of previous fade states (for state-aware color scheme transitions).
	 */
	private Map<Integer, ComponentState> prevStateMap;

	/**
	 * Map of next fade states (for state-aware color scheme transitions).
	 */
	private Map<Integer, ComponentState> nextStateMap;

	private ComponentListener substanceComponentListener;

	/**
	 * Listener for fade animations on list rollovers.
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

			fadeOutRolloverIndication();
			// System.out.println("Nulling RO index");
			resetRolloverIndex();
			// rolledOverIndex = -1;
			// list.putClientProperty(ROLLED_OVER_INDEX, null);
		}

		public void mouseMoved(MouseEvent e) {
			// if (SubstanceCoreUtilities.toBleedWatermark(list))
			// return;
			if (!list.isEnabled())
				return;
			handleMove(e);
		}

		public void mouseDragged(MouseEvent e) {
			// if (SubstanceCoreUtilities.toBleedWatermark(list))
			// return;

			if (!list.isEnabled())
				return;
			handleMove(e);
		}

		/**
		 * Handles various mouse move events and initiates the fade animation if
		 * necessary.
		 * 
		 * @param e
		 *            Mouse event.
		 */
		private void handleMove(MouseEvent e) {
			boolean fadeAllowed = !LafWidgetUtilities.hasNoFades(list,
					FadeKind.ROLLOVER);
			// no rollover effects on non-Substance renderers
			if (!(list.getCellRenderer() instanceof SubstanceDefaultListCellRenderer))
				fadeAllowed = false;

			if (!fadeAllowed) {
				fadeOutRolloverIndication();
				resetRolloverIndex();
				// rolledOverIndex = -1;
				// list.putClientProperty(ROLLED_OVER_INDEX, null);
				return;
			}

			int roIndex = list.locationToIndex(e.getPoint());
			if ((roIndex >= 0) && (roIndex < list.getModel().getSize())) {
				// test actual hit
				if (!list.getCellBounds(roIndex, roIndex)
						.contains(e.getPoint())) {
					roIndex = -1;
				}
			}
			if ((roIndex < 0) || (roIndex >= list.getModel().getSize())) {
				fadeOutRolloverIndication();
				// System.out.println("Nulling RO index");
				resetRolloverIndex();
				// rolledOverIndex = -1;
				// list.putClientProperty(ROLLED_OVER_INDEX, null);
			} else {
				// check if this is the same index
				// Integer currRoIndex = (Integer) list
				// .getClientProperty(ROLLED_OVER_INDEX);
				if ((rolledOverIndex >= 0) && (rolledOverIndex == roIndex))
					return;

				fadeOutRolloverIndication();
				FadeTracker.getInstance().trackFadeIn(FadeKind.ROLLOVER, list,
						roIndex, false, new CellRepaintCallback(list, roIndex));
				// System.out.println("Setting RO index to " + roIndex);
				rolledOverIndex = roIndex;
				// list.putClientProperty(ROLLED_OVER_INDEX, roIndex);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceListUI();
	}

	/**
	 * Creates a UI delegate for list.
	 */
	public SubstanceListUI() {
		super();
		prevStateMap = new HashMap<Integer, ComponentState>();
		nextStateMap = new HashMap<Integer, ComponentState>();
		rolledOverIndex = -1;
		selectedIndices = new HashMap<Integer, Object>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicListUI#installDefaults()
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();
		isFileList = Boolean.TRUE.equals(list
				.getClientProperty("List.isFileList"));
		isLeftToRight = list.getComponentOrientation().isLeftToRight();

		if (SubstanceCoreUtilities.toDrawWatermark(list)) {
			list.setOpaque(false);
		}

		for (int i = 0; i < list.getModel().getSize(); i++) {
			if (list.isSelectedIndex(i)) {
				selectedIndices.put(i, list.getModel().getElementAt(i));
				prevStateMap.put(i, ComponentState.SELECTED);
			}
		}

		this.list.putClientProperty(SubstanceCoreUtilities.USE_HIGHLIGHT,
				Boolean.TRUE);
		// this.list.putClientProperty(SubstanceListUI.SELECTED_INDICES,
		// selected);
	}

	@Override
	protected void uninstallDefaults() {
		selectedIndices.clear();
		// this.list.putClientProperty(SubstanceListUI.SELECTED_INDICES, null);

		super.uninstallDefaults();
	}

	/**
	 * Repaints a single cell during the fade animation cycle.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class CellRepaintCallback extends UIThreadFadeTrackerAdapter {
		/**
		 * Associated list.
		 */
		protected JList list;

		/**
		 * Associated (animated) cell index.
		 */
		protected int cellIndex;

		/**
		 * Renderer component for the cell.
		 */
		private Component rendererComponent;

		/**
		 * Creates a new animation repaint callback.
		 * 
		 * @param list
		 *            Associated list.
		 * @param cellIndex
		 *            Associated (animated) cell index.
		 */
		public CellRepaintCallback(JList list, int cellIndex) {
			this.list = list;
			this.cellIndex = cellIndex;
			// verify if this index still exists in the model (the list may have
			// shrunk!) - issue 413
			if (cellIndex < list.getModel().getSize()) {
				this.rendererComponent = list.getCellRenderer()
						.getListCellRendererComponent(
								list,
								list.getModel().getElementAt(cellIndex),
								cellIndex,
								list.getSelectionModel().isSelectedIndex(
										cellIndex),
								list.hasFocus()
										&& (cellIndex == list
												.getLeadSelectionIndex()));
			}
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
			if ((SubstanceListUI.this.list == list)
					&& (cellIndex < list.getModel().getSize())) {
				ComponentState currState = getCellState(cellIndex,
						this.rendererComponent);
				if (currState == ComponentState.DEFAULT) {
					prevStateMap.remove(cellIndex);
					nextStateMap.remove(cellIndex);
				} else {
					prevStateMap.put(cellIndex, currState);
					nextStateMap.put(cellIndex, currState);
				}
				// System.out.println(tabIndex + "->"
				// + prevStateMap.get(tabIndex).name());
			}
			repaintCell();
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
			if ((SubstanceListUI.this.list == list)
					&& (cellIndex < list.getModel().getSize())) {
				ComponentState nextState = nextStateMap.get(cellIndex);
				if (nextState == null) {
					prevStateMap.remove(cellIndex);
				} else {
					prevStateMap.put(cellIndex, nextState);
				}
				// System.out.println(tabIndex + "->"
				// + prevStateMap.get(tabIndex).name());
			}
			repaintCell();
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
			if ((SubstanceListUI.this.list == list)
					&& (cellIndex < list.getModel().getSize())) {
				nextStateMap.put(cellIndex, getCellState(cellIndex,
						this.rendererComponent));
			}
			repaintCell();
		}

		/**
		 * Repaints the associated cell.
		 */
		private void repaintCell() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (SubstanceListUI.this.list == null) {
						// may happen if the LAF was switched in the meantime
						return;
					}
					try {
						maybeUpdateLayoutState();
						int cellCount = list.getModel().getSize();
						if ((cellCount > 0) && (cellIndex < cellCount)) {
							// need to retrieve the cell rectangle since the
							// cells can be moved while animating
							Rectangle rect = SubstanceListUI.this
									.getCellBounds(list, cellIndex, cellIndex);
							// System.out.println("Repainting " + cellIndex
							// + " at " + rect);
							list.repaint(rect);
						}
					} catch (RuntimeException re) {
						return;
					}
				}
			});
		}
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		// Add listener for the selection animation
		substanceFadeSelectionListener = new ListSelectionListener() {
			protected void cancelFades(Set<Long> initiatedFadeSequences) {
				FadeTracker fadeTrackerInstance = FadeTracker.getInstance();
				for (long fadeId : initiatedFadeSequences) {
					fadeTrackerInstance.cancelFadeInstance(fadeId);
				}
			}

			public void valueChanged(ListSelectionEvent e) {
				// optimization on large lists and large selections
				if (LafWidgetUtilities.hasNoFades(list, FadeKind.SELECTION))
					return;

				// no selection animations on non-Substance renderers
				if (!(list.getCellRenderer() instanceof SubstanceDefaultListCellRenderer))
					return;

				Set<Long> initiatedFadeSequences = new HashSet<Long>();
				boolean fadeCanceled = false;

				// if (SubstanceCoreUtilities.toBleedWatermark(list))
				// return;

				FadeTracker fadeTrackerInstance = FadeTracker.getInstance();
				// Map<Integer, Object> currSelected = (Map<Integer, Object>)
				// SubstanceListUI.this.list
				// .getClientProperty(SubstanceListUI.SELECTED_INDICES);
				for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
					if (i >= list.getModel().getSize())
						continue;
					if (list.isSelectedIndex(i)) {
						// check if was selected before
						if (!selectedIndices.containsKey(i)) {
							// start fading in
							// System.out.println("Fade in on index " + i);

							if (!fadeCanceled) {
								long fadeId = fadeTrackerInstance.trackFadeIn(
										FadeKind.SELECTION, list, i, false,
										new CellRepaintCallback(list, i));
								initiatedFadeSequences.add(fadeId);
								if (initiatedFadeSequences.size() > 25) {
									cancelFades(initiatedFadeSequences);
									initiatedFadeSequences.clear();
									fadeCanceled = true;
								}
							}

							selectedIndices.put(i, list.getModel()
									.getElementAt(i));
						}
					} else {
						// check if was selected before and still points to the
						// same element
						if (selectedIndices.containsKey(i)) {
							if (selectedIndices.get(i) == list.getModel()
									.getElementAt(i)) {
								// start fading out
								// System.out.println("Fade out on index " + i);

								if (!fadeCanceled) {
									long fadeId = fadeTrackerInstance
											.trackFadeOut(FadeKind.SELECTION,
													list, i, false,
													new CellRepaintCallback(
															list, i));
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
		list.getSelectionModel().addListSelectionListener(
				substanceFadeSelectionListener);

		substanceFadeRolloverListener = new RolloverFadeListener();
		list.addMouseMotionListener(substanceFadeRolloverListener);
		list.addMouseListener(substanceFadeRolloverListener);

		substancePropertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (SubstanceLookAndFeel.WATERMARK_VISIBLE.equals(evt
						.getPropertyName())) {
					list.setOpaque(!SubstanceCoreUtilities
							.toDrawWatermark(list));
				}
			}
		};
		list.addPropertyChangeListener(substancePropertyChangeListener);

		this.substanceComponentListener = new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				// clear the rollover indexes since these are no longer
				// in sync with the mouse location
				fadeOutRolloverIndication();
				resetRolloverIndex();
			}
		};
		this.list.addComponentListener(this.substanceComponentListener);
	}

	@Override
	protected void uninstallListeners() {
		list.getSelectionModel().removeListSelectionListener(
				substanceFadeSelectionListener);
		substanceFadeSelectionListener = null;

		list.removeMouseMotionListener(substanceFadeRolloverListener);
		list.removeMouseListener(substanceFadeRolloverListener);
		substanceFadeRolloverListener = null;

		list.removePropertyChangeListener(substancePropertyChangeListener);
		substancePropertyChangeListener = null;

		this.list.removeComponentListener(this.substanceComponentListener);
		this.substanceComponentListener = null;

		super.uninstallListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicListUI#paintCell(java.awt.Graphics, int,
	 * java.awt.Rectangle, javax.swing.ListCellRenderer, javax.swing.ListModel,
	 * javax.swing.ListSelectionModel, int)
	 */
	@Override
	protected void paintCell(Graphics g, int row, Rectangle rowBounds,
			ListCellRenderer cellRenderer, ListModel dataModel,
			ListSelectionModel selModel, int leadIndex) {
		Object value = dataModel.getElementAt(row);
		boolean cellHasFocus = list.hasFocus() && (row == leadIndex);
		boolean isSelected = selModel.isSelectedIndex(row);

		Component rendererComponent = cellRenderer
				.getListCellRendererComponent(list, value, row, isSelected,
						cellHasFocus);

		if (!(rendererComponent instanceof SubstanceDefaultListCellRenderer)) {
			// if it's not Substance renderer - ask the Basic delegate to paint
			// it.
			super.paintCell(g, row, rowBounds, cellRenderer, dataModel,
					selModel, leadIndex);
			return;
		}

		boolean isWatermarkBleed = updateInfo.toDrawWatermark;

		int cx = rowBounds.x;
		int cy = rowBounds.y;
		int cw = rowBounds.width;
		int ch = rowBounds.height;

		if (isFileList) {
			// Shrink renderer to preferred size. This is mostly used on Windows
			// where selection is only shown around the file name, instead of
			// across the whole list cell.
			int w = Math
					.min(cw, rendererComponent.getPreferredSize().width + 4);
			if (!isLeftToRight) {
				cx += (cw - w);
			}
			cw = w;
		}

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(TransitionLayout.getAlphaComposite(list, g));
		if (!isWatermarkBleed) {
			Color background = rendererComponent.getBackground();
			// optimization - only render background if it's different
			// from the list background
			if ((background != null)
					&& (!list.getBackground().equals(background) || this.updateInfo.isInDecorationArea)) {
				g2d.setColor(background);
				g2d.fillRect(cx, cy, cw, ch);
			}
		} else {
			BackgroundPaintingUtils.fillAndWatermark(g2d, this.list,
					rendererComponent.getBackground(), new Rectangle(cx, cy,
							cw, ch));
		}

		ComponentState prevState = getPrevCellState(row);
		ComponentState currState = getCellState(row, rendererComponent);

		// Compute the alpha values for the animation.
		float startAlpha = this.updateInfo.getHighlightAlpha(prevState);
		float endAlpha = this.updateInfo.getHighlightAlpha(currState);

		// if the renderer is disabled, do not show any highlights
		float totalAlpha = endAlpha;
		float fadeCoef = 0.0f;
		if (rendererComponent.isEnabled()) {
			FadeState state = SubstanceFadeUtilities.getFadeState(list, row,
					FadeKind.SELECTION, FadeKind.ROLLOVER);
			if (state != null) {
				fadeCoef = state.getFadePosition();

				// compute the total alpha of the overlays.
				if (state.isFadingIn()) {
					totalAlpha = startAlpha + (endAlpha - startAlpha)
							* fadeCoef;
				} else {
					totalAlpha = startAlpha + (endAlpha - startAlpha)
							* (1.0f - fadeCoef);
				}

				if (state.isFadingIn())
					fadeCoef = 1.0f - fadeCoef;
			}
		}

		SubstanceColorScheme prevScheme = this.updateInfo
				.getHighlightColorScheme(prevState);
		SubstanceColorScheme currScheme = this.updateInfo
				.getHighlightColorScheme(currState);
		SubstanceColorScheme prevBorderScheme = this.updateInfo
				.getHighlightBorderColorScheme(prevState);
		SubstanceColorScheme currBorderScheme = this.updateInfo
				.getHighlightBorderColorScheme(currState);

		Rectangle cellRect = new Rectangle(cx, cy, cw, ch);
		if (totalAlpha > 0.0f) {
			g2d.setComposite(TransitionLayout.getAlphaComposite(list,
					totalAlpha, g));
			HighlightPainterUtils.paintHighlight(g2d, this.rendererPane,
					rendererComponent, cellRect, 0.8f, null, currScheme,
					prevScheme, currBorderScheme, prevBorderScheme, fadeCoef);
			g2d.setComposite(TransitionLayout.getAlphaComposite(list, g));
		}

		rendererPane.paintComponent(g2d, rendererComponent, list, cx, cy, cw,
				ch, true);
		g2d.dispose();
	}

	/**
	 * Returns the previous state for the specified cell.
	 * 
	 * @param cellIndex
	 *            Cell index.
	 * @return The previous state for the specified cell.
	 */
	public ComponentState getPrevCellState(int cellIndex) {
		if (prevStateMap.containsKey(cellIndex))
			return prevStateMap.get(cellIndex);
		return getCellState(cellIndex, null);
		// return ComponentState.DEFAULT;
	}

	/**
	 * Returns the current state for the specified cell.
	 * 
	 * @param cellIndex
	 *            Cell index.
	 * @param rendererComponent
	 *            Renderer component for the specified cell index.
	 * @return The current state for the specified cell.
	 */
	public ComponentState getCellState(int cellIndex,
			Component rendererComponent) {
		boolean isEnabled = this.list.isEnabled();
		if (rendererComponent != null) {
			isEnabled = isEnabled && rendererComponent.isEnabled();
		}
		boolean isRollover = (rolledOverIndex >= 0)
				&& (rolledOverIndex == cellIndex);
		boolean isSelected = list.isSelectedIndex(cellIndex);
		return ComponentState.getState(isEnabled, isRollover, isSelected);
	}

	/**
	 * Resets the rollover index.
	 */
	public void resetRolloverIndex() {
		rolledOverIndex = -1;
	}

	/**
	 * Initiates the fade out effect.
	 */
	private void fadeOutRolloverIndication() {
		// Integer prevRoIndex = (Integer) list
		// .getClientProperty(ROLLED_OVER_INDEX);
		// if (prevRoIndex == null)
		// return;
		if (rolledOverIndex < 0)
			return;

		FadeTracker.getInstance().trackFadeOut(FadeKind.ROLLOVER, list,
				rolledOverIndex, false,
				new CellRepaintCallback(list, rolledOverIndex));
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
	 * Returns the current default color scheme. This method is for internal use
	 * only.
	 * 
	 * @return The current default color scheme.
	 */
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

	private UpdateOptimizationInfo updateInfo;

	private class UpdateOptimizationInfo {
		public boolean toDrawWatermark;

		private Map<ComponentState, SubstanceColorScheme> highlightSchemeMap;

		private Map<ComponentState, SubstanceColorScheme> borderSchemeMap;

		private Map<ComponentState, Float> highlightAlphaMap;

		public SubstanceColorScheme defaultScheme;

		public Map<Component, Boolean> opacity;

		public DecorationAreaType decorationAreaType;

		public boolean isInDecorationArea;

		public UpdateOptimizationInfo() {
			this.toDrawWatermark = SubstanceCoreUtilities.toDrawWatermark(list);
			this.defaultScheme = SubstanceColorSchemeUtilities.getColorScheme(
					list, ComponentState.DEFAULT);
			this.highlightAlphaMap = new EnumMap<ComponentState, Float>(
					ComponentState.class);
			this.highlightSchemeMap = new EnumMap<ComponentState, SubstanceColorScheme>(
					ComponentState.class);
			this.borderSchemeMap = new EnumMap<ComponentState, SubstanceColorScheme>(
					ComponentState.class);
			this.opacity = new HashMap<Component, Boolean>();
			this.decorationAreaType = SubstanceLookAndFeel
					.getDecorationType(list);

			SubstanceSkin skin = SubstanceCoreUtilities.getSkin(list);
			this.isInDecorationArea = (this.decorationAreaType != null)
					&& skin
							.isRegisteredAsDecorationArea(this.decorationAreaType)
					&& TransitionLayout.isOpaque(list);
		}

		public SubstanceColorScheme getHighlightColorScheme(ComponentState state) {
			if (!this.highlightSchemeMap.containsKey(state)) {
				this.highlightSchemeMap.put(state,
						SubstanceColorSchemeUtilities.getColorScheme(list,
								ColorSchemeAssociationKind.HIGHLIGHT, state));
			}
			return this.highlightSchemeMap.get(state);
		}

		public SubstanceColorScheme getHighlightBorderColorScheme(
				ComponentState state) {
			if (!this.borderSchemeMap.containsKey(state)) {
				this.borderSchemeMap.put(state, SubstanceColorSchemeUtilities
						.getColorScheme(list,
								ColorSchemeAssociationKind.HIGHLIGHT_BORDER,
								state));
			}
			return this.borderSchemeMap.get(state);
		}

		public float getHighlightAlpha(ComponentState state) {
			if (!this.highlightAlphaMap.containsKey(state)) {
				this.highlightAlphaMap.put(state, SubstanceColorSchemeUtilities
						.getHighlightAlpha(list, state));
			}
			return this.highlightAlphaMap.get(state);
		}
	}
}
