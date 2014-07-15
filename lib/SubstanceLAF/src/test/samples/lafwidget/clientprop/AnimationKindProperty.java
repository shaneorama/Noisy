/*
 * Copyright (c) 2005-2008 Substance Kirill Grouchnikov. All Rights Reserved.
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
package test.samples.lafwidget.clientprop;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.lafwidget.LafWidgetUtilities;
import org.jvnet.lafwidget.utils.LafConstants.AnimationKind;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.renderers.SubstanceDefaultComboBoxRenderer;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;

/**
 * Test application that shows the use of the {@link LafWidget#ANIMATION_KIND}
 * client property.
 * 
 * @author Kirill Grouchnikov
 * @see LafWidget#ANIMATION_KIND
 */
public class AnimationKindProperty extends JFrame {
	/**
	 * Creates the main frame for <code>this</code> sample.
	 */
	public AnimationKindProperty() {
		super("Animation kind");

		setLayout(new BorderLayout());

		final JButton button1 = new JButton("button");

		JPanel main = new JPanel(new FlowLayout(FlowLayout.CENTER));
		this.add(main, BorderLayout.CENTER);
		main.add(button1);

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		final JComboBox animationKindCombo = new JComboBox(new Object[] {
				AnimationKind.NONE, AnimationKind.FAST, AnimationKind.REGULAR,
				AnimationKind.SLOW, AnimationKind.DEBUG_FAST,
				AnimationKind.DEBUG, AnimationKind.DEBUG_SLOW });
		animationKindCombo.setRenderer(new SubstanceDefaultComboBoxRenderer(
				animationKindCombo) {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				AnimationKind kind = (AnimationKind) value;
				return super.getListCellRendererComponent(list, kind.getName(),
						index, isSelected, cellHasFocus);
			}
		});
		animationKindCombo.setSelectedItem(LafWidgetUtilities
				.getAnimationKind(button1));
		animationKindCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button1.putClientProperty(LafWidget.ANIMATION_KIND,
						animationKindCombo.getSelectedItem());
				button1.requestFocus();
			}
		});
		controls.add(new JLabel("Animation kind"));
		controls.add(animationKindCombo);
		this.add(controls, BorderLayout.SOUTH);

		this.setSize(400, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * The main method for <code>this</code> sample. The arguments are
	 * ignored.
	 * 
	 * @param args
	 *            Ignored.
	 */
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
				new AnimationKindProperty().setVisible(true);
			}
		});
	}
}
