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
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.lafwidget.animation.FadeTracker;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.api.ComponentState;
import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.painter.border.SubstanceBorderPainter;
import org.jvnet.substance.painter.gradient.SubstanceGradientPainter;
import org.jvnet.substance.utils.*;

/**
 * UI for progress bars in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceProgressBarUI extends BasicProgressBarUI {
	/**
	 * Hash for computed stripe images.
	 */
	private static LazyResettableHashMap<BufferedImage> stripeMap = new LazyResettableHashMap<BufferedImage>(
			"SubstanceProgressBarUI.stripeMap");

	/**
	 * Hash for computed background images.
	 */
	private static LazyResettableHashMap<BufferedImage> backgroundMap = new LazyResettableHashMap<BufferedImage>(
			"SubstanceProgressBarUI.backgroundMap");

	/**
	 * The current state of the indeterminate animation's cycle. 0, the initial
	 * value, means paint the first frame. When the progress bar is
	 * indeterminate and showing, the default animation thread updates this
	 * variable by invoking incrementAnimationIndex() every repaintInterval
	 * milliseconds.
	 */
	private float animationIndex;

	/**
	 * Value change listener on the associated progress bar.
	 */
	protected ChangeListener substanceValueChangeListener;

	/**
	 * Property change listener. Tracks changes to the <code>font</code>
	 * property.
	 */
	protected PropertyChangeListener substancePropertyChangeListener;

	/**
	 * Inner margin.
	 */
	protected int margin;

	/**
	 * The speed factor for the indeterminate progress bars.
	 */
	protected float speed;

	/**
	 * Fade kind for the progress bar value change.
	 */
	public static final FadeKind PROGRESS_BAR_VALUE_CHANGED = new FadeKind(
			"substancelaf.progressBarValueChanged");

	/**
	 * Property name for storing the <code>from</code> value on progress bar
	 * value animation. Is for internal use only.
	 */
	private static final String PROGRESS_BAR_FROM = "substancelaf.internal.from";

	/**
	 * Property name for storing the <code>to</code> value on progress bar value
	 * animation. Is for internal use only.
	 */
	private static final String PROGRESS_BAR_TO = "substancelaf.internal.to";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceProgressBarUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		progressBar.putClientProperty(PROGRESS_BAR_TO, progressBar.getValue());
		LookAndFeel.installProperty(progressBar, "opaque", Boolean.FALSE);

		this.speed = (20.0f * UIManager.getInt("ProgressBar.repaintInterval"))
				/ UIManager.getInt("ProgressBar.cycleTime");

		float borderThickness = SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(this.progressBar));
		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(this.progressBar);
		this.margin = borderPainter.isPaintingInnerContour() ? (int) Math
				.ceil(2.0 * borderThickness) : (int) Math
				.ceil(1.5 * borderThickness);
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		final BoundedRangeModel model = progressBar.getModel();
		if (model instanceof DefaultBoundedRangeModel) {
			substanceValueChangeListener = new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					SubstanceCoreUtilities
							.testComponentStateChangeThreadingViolation(progressBar);

					int oldValue = (Integer) progressBar
							.getClientProperty(PROGRESS_BAR_TO);
					int currValue = model.getValue();
					int span = model.getMaximum() - model.getMinimum();
					// Insets b = getInsets(); // area for border
					int barRectWidth = progressBar.getWidth() - 2 * margin;
					int barRectHeight = progressBar.getHeight() - 2 * margin;
					int totalPixels = (progressBar.getOrientation() == JProgressBar.HORIZONTAL) ? barRectWidth
							: barRectHeight;
					// fix for defect 223 (min and max on the model are the
					// same).
					int pixelDelta = (span <= 0) ? 0 : (currValue - oldValue)
							* totalPixels / span;

					FadeTracker fadeTracker = FadeTracker.getInstance();
					if (!fadeTracker.isTracked(progressBar,
							PROGRESS_BAR_VALUE_CHANGED)) {
						progressBar.putClientProperty(PROGRESS_BAR_FROM,
								progressBar.getClientProperty(PROGRESS_BAR_TO));
					}
					progressBar.putClientProperty(PROGRESS_BAR_TO, progressBar
							.getValue());

					// do not animate progress bars used in cell renderers
					// since in this case it will most probably be the
					// same progress bar used to display different
					// values for different cells.
					boolean isInCellRenderer = (SwingUtilities
							.getAncestorOfClass(CellRendererPane.class,
									progressBar) != null);
					if (!isInCellRenderer && Math.abs(pixelDelta) > 5) {
						FadeTracker.getInstance().trackFadeIn(
								PROGRESS_BAR_VALUE_CHANGED, progressBar, false,
								null);
					}
				}
			};
			((DefaultBoundedRangeModel) model)
					.addChangeListener(substanceValueChangeListener);
		}

		this.substancePropertyChangeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("font".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (progressBar != null)
								progressBar.updateUI();
						}
					});
				}
			}
		};
		this.progressBar
				.addPropertyChangeListener(this.substancePropertyChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicProgressBarUI#uninstallListeners()
	 */
	@Override
	protected void uninstallListeners() {
		BoundedRangeModel model = progressBar.getModel();
		if (model instanceof DefaultBoundedRangeModel) {
			((DefaultBoundedRangeModel) model)
					.removeChangeListener(substanceValueChangeListener);
		}

		this.progressBar
				.removePropertyChangeListener(this.substancePropertyChangeListener);
		this.substancePropertyChangeListener = null;

		super.uninstallListeners();
	}

	/**
	 * Retrieves stripe image.
	 * 
	 * @param baseSize
	 *            Stripe base in pixels.
	 * @param isRotated
	 *            if <code>true</code>, the resulting stripe image will be
	 *            rotated.
	 * @param colorScheme
	 *            Color scheme to paint the stripe image.
	 * @return Stripe image.
	 */
	private static BufferedImage getStripe(int baseSize, boolean isRotated,
			SubstanceColorScheme colorScheme) {
		HashMapKey key = SubstanceCoreUtilities.getHashKey(baseSize, isRotated,
				colorScheme.getDisplayName());
		BufferedImage result = SubstanceProgressBarUI.stripeMap.get(key);
		if (result == null) {
			result = SubstanceImageCreator.getStripe(baseSize, colorScheme
					.getUltraLightColor());
			if (isRotated) {
				result = SubstanceImageCreator.getRotated(result, 1);
			}
			SubstanceProgressBarUI.stripeMap.put(key, result);
		}
		return result;
	}

	/**
	 * Returns the background of a determinate progress bar.
	 * 
	 * @param bar
	 *            Progress bar.
	 * @param width
	 *            Progress bar width.
	 * @param height
	 *            Progress bar height.
	 * @param scheme
	 *            Color scheme for the background.
	 * @param gp
	 *            Gradient painter.
	 * @param orientation
	 *            Progress bar orientation (vertical / horizontal).
	 * @param componentOrientation
	 *            Progress bar LTR / RTL orientation.
	 * @return Background image.
	 */
	private static BufferedImage getDeterminateBackground(JProgressBar bar,
			int width, int height, SubstanceColorScheme scheme,
			SubstanceGradientPainter gp, int orientation,
			ComponentOrientation componentOrientation) {
		HashMapKey key = SubstanceCoreUtilities.getHashKey(width, height,
				scheme.getDisplayName(), gp.getDisplayName(), orientation,
				componentOrientation);
		BufferedImage result = SubstanceProgressBarUI.backgroundMap.get(key);
		if (result == null) {
			result = SubstanceCoreUtilities.getBlankImage(width, height);
			Graphics2D g2d = result.createGraphics();
			Shape contour = SubstanceOutlineUtilities.getBaseOutline(width,
					height, 0, null);
			gp.paintContourBackground(g2d, bar, width, height, contour, false,
					scheme, scheme, 0, true, false);
			g2d.dispose();

			if (orientation == SwingConstants.VERTICAL) {
				if (componentOrientation.isLeftToRight())
					result = SubstanceImageCreator.getRotated(result, 3);
				else
					result = SubstanceImageCreator.getRotated(result, 1);
			}
			SubstanceProgressBarUI.backgroundMap.put(key, result);
		}
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicProgressBarUI#paintDeterminate(java.awt.Graphics
	 * , javax.swing.JComponent)
	 */
	@Override
	public void paintDeterminate(Graphics g, JComponent c) {
		if (!(g instanceof Graphics2D)) {
			return;
		}

		// final Insets insets = this.getInsets();
		// insets.top /= 2;
		// insets.left /= 2;
		// insets.bottom /= 2;
		// insets.right /= 2;
		int barRectWidth = progressBar.getWidth() - 2 * margin;
		int barRectHeight = progressBar.getHeight() - 2 * margin;

		// amount of progress to draw
		int amountFull = getAmountFull(new Insets(margin, margin, margin,
				margin), barRectWidth, barRectHeight);

		Graphics2D g2d = (Graphics2D) g.create();
		// install state-aware alpha channel (support for skins
		// that use translucency on disabled states).
		float stateAlpha = SubstanceColorSchemeUtilities.getAlpha(progressBar,
				progressBar.isEnabled() ? ComponentState.DEFAULT
						: ComponentState.DISABLED_UNSELECTED);
		g2d.setComposite(TransitionLayout.getAlphaComposite(progressBar,
				stateAlpha, g));

		SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
				.getColorScheme(progressBar,
						progressBar.isEnabled() ? ComponentState.DEFAULT
								: ComponentState.DISABLED_UNSELECTED);

		SubstanceGradientPainter gp = SubstanceCoreUtilities
				.getGradientPainter(progressBar);
		if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
			BufferedImage back = getDeterminateBackground(progressBar,
					barRectWidth + 1, barRectHeight + 1, scheme, gp,
					progressBar.getOrientation(), this.progressBar
							.getComponentOrientation());
			g2d.drawImage(back, margin, margin, null);
		} else {
			BufferedImage back = getDeterminateBackground(progressBar,
					barRectHeight + 1, barRectWidth + 1, scheme, gp,
					progressBar.getOrientation(), this.progressBar
							.getComponentOrientation());
			g2d.drawImage(back, margin, margin, null);
		}

		if (amountFull > 0) {
			// float borderStrokeWidth = SubstanceSizeUtils
			// .getBorderStrokeWidth(SubstanceSizeUtils
			// .getComponentFontSize(progressBar));
			// SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
			// .getBorderPainter(null);
			int borderDelta = 0;
			// (borderPainter instanceof InnerDelegateBorderPainter) ? (int)
			// (Math
			// .ceil(2.0 * borderStrokeWidth)) / 2
			// : (int) (Math.ceil(borderStrokeWidth / 2.0));// : 0;
			// if (borderDelta == 0)

			SubstanceColorScheme fillColorScheme = SubstanceColorSchemeUtilities
					.getColorScheme(progressBar,
							progressBar.isEnabled() ? ComponentState.SELECTED
									: ComponentState.DISABLED_UNSELECTED);
			if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
				int barWidth = amountFull - 2 * borderDelta;
				int barHeight = barRectHeight - 2 * borderDelta;
				if ((barWidth > 0) && (barHeight > 0)) {
					if (progressBar.getComponentOrientation().isLeftToRight()) {
						SubstanceImageCreator.paintRectangularBackground(
								progressBar, g, margin + borderDelta, margin
										+ borderDelta, barWidth, barHeight,
								fillColorScheme, 0.6f, false);

						// g.setColor(Color.red);
						// g.fillRect(insets.left + borderDelta,
						// insets.top + borderDelta, barWidth, barHeight);
					} else {
						// fix for RTL determinate horizontal progress
						// bar in 2.3
						SubstanceImageCreator.paintRectangularBackground(
								progressBar, g, margin + barRectWidth
										- amountFull - 2 * borderDelta, margin
										+ borderDelta, barWidth, barHeight,
								fillColorScheme, 0.6f, false);
					}
				}
			} else { // VERTICAL
				int barWidth = amountFull - 2 * borderDelta;
				int barHeight = barRectWidth - 2 * borderDelta;
				if ((amountFull > 0) && (barHeight > 0)) {
					// fix for issue 95. Vertical bar is growing from
					// the bottom
					SubstanceImageCreator.paintRectangularBackground(
							progressBar, g, margin + borderDelta, margin
									+ barRectHeight - barWidth - borderDelta,
							barHeight, barWidth, fillColorScheme, 0.6f, true);
				}
			}
		}

		// Deal with possible text painting
		if (progressBar.isStringPainted()) {
			g2d.setComposite(TransitionLayout.getAlphaComposite(progressBar,
					1.0f, g));
			this.paintString(g2d, margin, margin, barRectWidth, barRectHeight,
					amountFull, new Insets(margin, margin, margin, margin));
		}
		g2d.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicProgressBarUI#getSelectionBackground()
	 */
	@Override
	protected Color getSelectionBackground() {
		SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
				.getColorScheme(progressBar,
						progressBar.isEnabled() ? ComponentState.DEFAULT
								: ComponentState.DISABLED_UNSELECTED);
		return SubstanceColorUtilities.getForegroundColor(scheme);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicProgressBarUI#getSelectionForeground()
	 */
	@Override
	protected Color getSelectionForeground() {
		SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
				.getColorScheme(progressBar,
						progressBar.isEnabled() ? ComponentState.SELECTED
								: ComponentState.DISABLED_UNSELECTED);
		return SubstanceColorUtilities.getForegroundColor(scheme);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicProgressBarUI#paintIndeterminate(java.awt
	 * .Graphics, javax.swing.JComponent)
	 */
	@Override
	public void paintIndeterminate(Graphics g, JComponent c) {
		if (!(g instanceof Graphics2D)) {
			return;
		}

		// final Insets b = this.getInsets(); // area for border
		final int barRectWidth = progressBar.getWidth() - 2 * margin;
		final int barRectHeight = progressBar.getHeight() - 2 * margin;

		final int valComplete = (int) animationIndex;

		Graphics2D g2d = (Graphics2D) g.create();
		// install state-aware alpha channel (support for skins
		// that use translucency on disabled states).
		float stateAlpha = SubstanceColorSchemeUtilities.getAlpha(progressBar,
				progressBar.isEnabled() ? ComponentState.SELECTED
						: ComponentState.DISABLED_UNSELECTED);
		g2d.setComposite(TransitionLayout.getAlphaComposite(progressBar,
				stateAlpha, g));

		SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
				.getColorScheme(progressBar,
						progressBar.isEnabled() ? ComponentState.SELECTED
								: ComponentState.DISABLED_UNSELECTED);
		if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
			SubstanceImageCreator.paintRectangularStripedBackground(
					progressBar, g2d, margin, margin, barRectWidth,
					barRectHeight, scheme, SubstanceProgressBarUI.getStripe(
							barRectHeight, false, scheme), valComplete, 0.6f,
					false);
		} else {
			// fix for issue 95. Vertical progress bar grows from the
			// bottom.
			SubstanceImageCreator.paintRectangularStripedBackground(
					progressBar, g2d, margin, margin, barRectWidth,
					barRectHeight, scheme, SubstanceProgressBarUI.getStripe(
							barRectWidth, true, scheme), 2 * barRectWidth
							- valComplete, 0.6f, true);
		}

		// Deal with possible text painting
		if (progressBar.isStringPainted()) {
			g2d.setComposite(TransitionLayout.getAlphaComposite(progressBar,
					1.0f, g));
			this.paintString(g2d, margin, margin, barRectWidth, barRectHeight,
					barRectWidth, new Insets(margin, margin, margin, margin));
		}
		g2d.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicProgressBarUI#getBox(java.awt.Rectangle)
	 */
	@Override
	protected Rectangle getBox(Rectangle r) {
		// Insets b = this.getInsets(); // area for border
		int barRectWidth = progressBar.getWidth() - 2 * margin;
		int barRectHeight = progressBar.getHeight() - 2 * margin;
		return new Rectangle(margin, margin, barRectWidth, barRectHeight);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicProgressBarUI#incrementAnimationIndex()
	 */
	@Override
	protected void incrementAnimationIndex() {
		float newValue = animationIndex + this.speed;

		// Insets b = this.getInsets(); // area for border
		int barRectHeight = progressBar.getHeight() - 2 * margin;
		int barRectWidth = progressBar.getWidth() - 2 * margin;
		int threshold = 0;
		if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
			threshold = 2 * barRectHeight + 1;
		} else {
			threshold = 2 * barRectWidth + 1;
		}
		animationIndex = newValue % threshold;
		progressBar.repaint();
	}

	/**
	 * Returns the memory usage string.
	 * 
	 * @return The memory usage string.
	 */
	public static String getMemoryUsage() {
		StringBuffer sb = new StringBuffer();
		sb.append("SubstanceProgressBarUI: \n");
		sb.append("\t" + SubstanceProgressBarUI.stripeMap.size() + " stripes");
		return sb.toString();
	}

	@Override
	protected int getAmountFull(Insets b, int width, int height) {
		int amountFull = 0;
		BoundedRangeModel model = progressBar.getModel();

		long span = model.getMaximum() - model.getMinimum();
		double currentValue = model.getValue();
		FadeTracker fadeTracker = FadeTracker.getInstance();
		if (fadeTracker.isTracked(progressBar, PROGRESS_BAR_VALUE_CHANGED)) {
			double fade = fadeTracker.getFade(progressBar,
					PROGRESS_BAR_VALUE_CHANGED);
			int from = (Integer) progressBar
					.getClientProperty(PROGRESS_BAR_FROM);
			int to = (Integer) progressBar.getClientProperty(PROGRESS_BAR_TO);
			currentValue = from + (fade * (to - from));
		}

		double percentComplete = (currentValue - model.getMinimum()) / span;

		if ((model.getMaximum() - model.getMinimum()) != 0) {
			if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
				amountFull = (int) Math.round(width * percentComplete);
			} else {
				amountFull = (int) Math.round(height * percentComplete);
			}
		}
		return amountFull;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicProgressBarUI#getPreferredInnerHorizontal()
	 */
	@Override
	protected Dimension getPreferredInnerHorizontal() {
		int size = SubstanceSizeUtils.getComponentFontSize(this.progressBar);
		size += 2 * SubstanceSizeUtils.getAdjustedSize(size, 1, 4, 1, false);
		return new Dimension(146 + SubstanceSizeUtils.getAdjustedSize(size, 0,
				1, 10, false), size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicProgressBarUI#getPreferredInnerVertical()
	 */
	@Override
	protected Dimension getPreferredInnerVertical() {
		int size = SubstanceSizeUtils.getComponentFontSize(this.progressBar);
		size += 2 * SubstanceSizeUtils.getAdjustedSize(size, 1, 4, 1, false);
		return new Dimension(size, 146 + SubstanceSizeUtils.getAdjustedSize(
				size, 0, 1, 10, false));
	}

	@Override
	protected void paintString(Graphics g, int x, int y, int width, int height,
			int amountFull, Insets b) {
		if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
			if (progressBar.getComponentOrientation().isLeftToRight()) {
				if (progressBar.isIndeterminate()) {
					boxRect = getBox(boxRect);
					paintString(g, x, y, width, height, boxRect.x,
							boxRect.width, b);
				} else {
					paintString(g, x, y, width, height, x, amountFull, b);
				}
			} else {
				paintString(g, x, y, width, height, x + width - amountFull,
						amountFull, b);
			}
		} else {
			if (progressBar.isIndeterminate()) {
				boxRect = getBox(boxRect);
				paintString(g, x, y, width, height, boxRect.y, boxRect.height,
						b);
			} else {
				paintString(g, x, y, width, height, y + height - amountFull,
						amountFull, b);
			}
		}
	}

	/**
	 * Paints the progress string.
	 * 
	 * @param g
	 *            Graphics used for drawing.
	 * @param x
	 *            x location of bounding box
	 * @param y
	 *            y location of bounding box
	 * @param width
	 *            width of bounding box
	 * @param height
	 *            height of bounding box
	 * @param fillStart
	 *            start location, in x or y depending on orientation, of the
	 *            filled portion of the progress bar.
	 * @param amountFull
	 *            size of the fill region, either width or height depending upon
	 *            orientation.
	 * @param b
	 *            Insets of the progress bar.
	 */
	private void paintString(Graphics g, int x, int y, int width, int height,
			int fillStart, int amountFull, Insets b) {
		String progressString = progressBar.getString();
		Rectangle renderRectangle = getStringRectangle(progressString, x, y,
				width, height);

		if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
			SubstanceTextUtilities.paintText(g, this.progressBar,
					renderRectangle, progressString, -1, progressBar.getFont(),
					getSelectionBackground(), new Rectangle(amountFull, y,
							progressBar.getWidth() - amountFull, height));
			SubstanceTextUtilities.paintText(g, this.progressBar,
					renderRectangle, progressString, -1, progressBar.getFont(),
					getSelectionForeground(), new Rectangle(fillStart, y,
							amountFull, height));
		} else { // VERTICAL
			SubstanceTextUtilities.paintVerticalText(g, this.progressBar,
					renderRectangle, progressString, -1, progressBar.getFont(),
					getSelectionBackground(), new Rectangle(x, y, width,
							progressBar.getHeight() - amountFull), progressBar
							.getComponentOrientation().isLeftToRight());
			SubstanceTextUtilities.paintVerticalText(g, this.progressBar,
					renderRectangle, progressString, -1, progressBar.getFont(),
					getSelectionForeground(), new Rectangle(x, fillStart,
							width, amountFull), progressBar
							.getComponentOrientation().isLeftToRight());
		}
	}

	/**
	 * Returns the rectangle for the progress bar string.
	 * 
	 * @param progressString
	 *            Progress bar string.
	 * @param x
	 *            x location of bounding box
	 * @param y
	 *            y location of bounding box
	 * @param width
	 *            width of bounding box
	 * @param height
	 *            height of bounding box
	 * @return The rectangle for the progress bar string.
	 */
	protected Rectangle getStringRectangle(String progressString, int x, int y,
			int width, int height) {
		FontMetrics fontSizer = progressBar.getFontMetrics(progressBar
				.getFont());

		int stringWidth = fontSizer.stringWidth(progressString);

		if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
			return new Rectangle(x + Math.round(width / 2 - stringWidth / 2), y
					+ (height - fontSizer.getHeight()) / 2, stringWidth,
					fontSizer.getHeight());
		} else {
			return new Rectangle(x + (width - fontSizer.getHeight()) / 2, y
					+ Math.round(height / 2 - stringWidth / 2), fontSizer
					.getHeight(), stringWidth);
			// return new Rectangle(x - height / 2 - fontSizer.getHeight(), y,
			// fontSizer.getHeight(), stringWidth);
		}
	}
	//
	// private Insets getInsets() {
	// float borderThickness = SubstanceSizeUtils
	// .getBorderStrokeWidth(SubstanceSizeUtils
	// .getComponentFontSize(this.progressBar));
	// SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
	// .getBorderPainter(this.progressBar);
	// int insets = 0;
	// if (borderPainter instanceof InnerDelegateBorderPainter) {
	// insets = (int) Math.ceil(2.0 * borderThickness);
	// } else {
	// insets = (int) Math.ceil(1.5 * borderThickness);
	// }
	//
	// return new Insets(insets, insets, insets, insets);
	// }
}
