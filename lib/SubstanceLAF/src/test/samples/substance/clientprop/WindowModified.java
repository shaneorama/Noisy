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
package test.samples.substance.clientprop;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;

/**
 * Test application that shows the use of the
 * {@link SubstanceLookAndFeel#WINDOW_MODIFIED} client property.
 * 
 * @author Kirill Grouchnikov
 * @see SubstanceLookAndFeel#WINDOW_MODIFIED
 */
public class WindowModified extends JFrame {
	/**
	 * Creates the main frame for <code>this</code> sample.
	 */
	public WindowModified() {
		super("Window modified");

		this.setLayout(new BorderLayout());

		this.add(new JLabel("Type something in the text pane "
				+ "and hover over the close button"), BorderLayout.NORTH);

		// create a text pane
		JTextPane textArea = new JTextPane();
		this.add(textArea, BorderLayout.CENTER);
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			private void handleChange() {
				// on any document change, mark root pane as modified
				getRootPane().putClientProperty(
						SubstanceLookAndFeel.WINDOW_MODIFIED, Boolean.TRUE);
			}

			public void changedUpdate(DocumentEvent e) {
				handleChange();
			}

			public void insertUpdate(DocumentEvent e) {
				handleChange();
			}

			public void removeUpdate(DocumentEvent e) {
				handleChange();
			}
		});

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// on button click, mark root pane as not modified
				getRootPane().putClientProperty(
						SubstanceLookAndFeel.WINDOW_MODIFIED, Boolean.FALSE);
			}
		});

		buttons.add(saveButton);
		this.add(buttons, BorderLayout.SOUTH);

		this.setSize(400, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * The main method for <code>this</code> sample. The arguments are ignored.
	 * 
	 * @param args
	 *            Ignored.
	 */
	public static void main(String... args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
				new WindowModified().setVisible(true);
			}
		});
	}
}
