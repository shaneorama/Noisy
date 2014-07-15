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
import javax.swing.plaf.*;
import javax.swing.tree.TreeCellRenderer;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.SubstanceTreeUI;
import org.jvnet.substance.SubstanceTreeUI.TreePathId;
import org.jvnet.substance.api.*;
import org.jvnet.substance.utils.*;

/**
 * Default renderer for tree cells.
 * 
 * @author Kirill Grouchnikov
 */
@SubstanceApi
@SubstanceRenderer
public class SubstanceDefaultTreeCellRenderer extends JLabel implements
		TreeCellRenderer {
	/** Last tree the renderer was painted in. */
	private JTree tree;

	/** Is the value currently selected. */
	protected boolean selected;

	/** True if has focus. */
	protected boolean hasFocus;

	/**
	 * Returns a new instance of SubstanceDefaultTreeCellRenderer. Alignment is
	 * set to left aligned. Icons and text color are determined from the
	 * UIManager.
	 */
	public SubstanceDefaultTreeCellRenderer() {
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
	}

	/**
	 * Returns the default icon that is used to represent non-leaf nodes that
	 * are expanded.
	 * 
	 * @return The default icon for non-leaf expanded nodes.
	 */
	public Icon getDefaultOpenIcon() {
		return UIManager.getIcon("Tree.openIcon");
	}

	/**
	 * Returns the default icon that is used to represent non-leaf nodes that
	 * are not expanded.
	 * 
	 * @return The default icon for non-leaf non-expanded nodes.
	 */
	public Icon getDefaultClosedIcon() {
		return UIManager.getIcon("Tree.closedIcon");
	}

	/**
	 * Returns the default icon that is used to represent leaf nodes.
	 * 
	 * @return The default icon for leaf nodes.
	 */
	public Icon getDefaultLeafIcon() {
		return UIManager.getIcon("Tree.leafIcon");
	}

	/**
	 * Subclassed to map <code>FontUIResource</code>s to null. If
	 * <code>font</code> is null, or a <code>FontUIResource</code>, this has the
	 * effect of letting the font of the JTree show through. On the other hand,
	 * if <code>font</code> is non-null, and not a <code>FontUIResource</code>,
	 * the font becomes <code>font</code>.
	 */
	@Override
	public void setFont(Font font) {
		if (font instanceof FontUIResource)
			font = null;
		super.setFont(font);
	}

	/**
	 * Gets the font of this component.
	 * 
	 * @return this component's font; if a font has not been set for this
	 *         component, the font of its parent is returned
	 */
	@Override
	public Font getFont() {
		Font font = super.getFont();

		if ((font == null) && (this.tree != null)) {
			// Strive to return a non-null value, otherwise the html support
			// will typically pick up the wrong font in certain situations.
			font = this.tree.getFont();
		}
		return font;
	}

	/**
	 * Configures the renderer based on the passed in components. The value is
	 * set from messaging the tree with <code>convertValueToText</code>, which
	 * ultimately invokes <code>toString</code> on <code>value</code>. The
	 * foreground color is set based on the selection and the icon is set based
	 * on on leaf and expanded.
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		String stringValue = tree.convertValueToText(value, sel, expanded,
				leaf, row, hasFocus);

		this.tree = tree;
		this.hasFocus = hasFocus;
		this.setText(stringValue);

		TreeUI treeUI = tree.getUI();
		if (treeUI instanceof SubstanceTreeUI) {
			SubstanceTreeUI ui = (SubstanceTreeUI) treeUI;
			TreePathId pathId = new TreePathId(tree.getPathForRow(row));
			ComponentState state = ui.getPathState(pathId);
			ComponentState prevState = ui.getPrevPathState(pathId);
			SubstanceColorScheme scheme = (state == ComponentState.DEFAULT) ? ui
					.getDefaultColorScheme()
					: SubstanceColorSchemeUtilities.getColorScheme(tree,
							ColorSchemeAssociationKind.HIGHLIGHT, state);
			if (scheme == null) {
				scheme = SubstanceColorSchemeUtilities.getColorScheme(tree,
						ColorSchemeAssociationKind.HIGHLIGHT, state);
			}

			SubstanceColorScheme prevScheme = scheme;
			if (prevState != state) {
				prevScheme = (prevState == ComponentState.DEFAULT) ? ui
						.getDefaultColorScheme()
						: SubstanceColorSchemeUtilities
								.getColorScheme(tree,
										ColorSchemeAssociationKind.HIGHLIGHT,
										prevState);
				if (prevScheme == null) {
					prevScheme = (prevState == ComponentState.DEFAULT) ? SubstanceColorSchemeUtilities
							.getColorScheme(tree, prevState)
							: SubstanceColorSchemeUtilities.getColorScheme(
									tree, ColorSchemeAssociationKind.HIGHLIGHT,
									prevState);
				}
			}

			Color color = SubstanceColorUtilities
					.getInterpolatedForegroundColor(tree, pathId, scheme,
							state, prevScheme, prevState, FadeKind.SELECTION,
							FadeKind.ROLLOVER);

			// System.out.println("[" + row + "," + column + "] - "
			// + prevState.name() + ":" + state.name() + ":" + color);

			super.setForeground(new ColorUIResource(color));
		} else {
			if (sel)
				this.setForeground(UIManager
						.getColor("Tree.selectionForeground"));
			else
				this.setForeground(UIManager.getColor("Tree.textForeground"));
		}

		if (SubstanceLookAndFeel.isCurrentLookAndFeel())
			SubstanceStripingUtils.applyStripedBackground(tree, row, this);

		// There needs to be a way to specify disabled icons.
		if (!tree.isEnabled()) {
			this.setEnabled(false);
			if (leaf) {
				this.setDisabledIcon(SubstanceImageCreator
						.toGreyscale(SubstanceImageCreator.makeTransparent(
								tree, this.getDefaultLeafIcon(), 0.5)));
			} else if (expanded) {
				this.setDisabledIcon(SubstanceImageCreator
						.toGreyscale(SubstanceImageCreator.makeTransparent(
								tree, this.getDefaultOpenIcon(), 0.5)));
				// setIcon(SubstanceImageCreator.toGreyscale(
				// SubstanceImageCreator
				// .makeTransparent(getDefaultOpenIcon(), 0.5)));
			} else {
				this.setDisabledIcon(SubstanceImageCreator
						.toGreyscale(SubstanceImageCreator.makeTransparent(
								tree, this.getDefaultClosedIcon(), 0.5)));
				// setIcon(SubstanceImageCreator.toGreyscale(
				// SubstanceImageCreator
				// .makeTransparent(getDefaultClosedIcon(), 0.5)));
			}
		} else {
			this.setEnabled(true);
			if (leaf) {
				this.setIcon(this.getDefaultLeafIcon());
			} else if (expanded) {
				this.setIcon(this.getDefaultOpenIcon());
			} else {
				this.setIcon(this.getDefaultClosedIcon());
			}
		}
		this.setComponentOrientation(tree.getComponentOrientation());

		this.setOpaque(false);

		this.selected = sel;

		if (treeUI instanceof SubstanceTreeUI) {
			SubstanceTreeUI ui = (SubstanceTreeUI) treeUI;
			Insets regInsets = ui.getCellRendererInsets();
			this
					.setBorder(new BorderUIResource.EmptyBorderUIResource(
							regInsets));
		}

		return this;
	}

	/**
	 * Overrides <code>JComponent.getPreferredSize</code> to return slightly
	 * wider preferred size value.
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension retDimension = super.getPreferredSize();

		if (retDimension != null)
			retDimension = new Dimension(retDimension.width + 3,
					retDimension.height);
		return retDimension;
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void validate() {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 * 
	 * @since 1.5
	 */
	@Override
	public void invalidate() {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void revalidate() {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void repaint(long tm, int x, int y, int width, int height) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void repaint(Rectangle r) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 * 
	 * @since 1.5
	 */
	@Override
	public void repaint() {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		if ("text".equals(propertyName))
			super.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(String propertyName, byte oldValue,
			byte newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(String propertyName, char oldValue,
			char newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(String propertyName, short oldValue,
			short newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(String propertyName, long oldValue,
			long newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(String propertyName, float oldValue,
			float newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(String propertyName, double oldValue,
			double newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
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
