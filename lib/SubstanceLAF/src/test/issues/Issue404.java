package test.issues;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel;

public class Issue404 extends JPanel {
	private String aText;

	public Issue404() {
		final JTextField textField = new JTextField(20);
		add(textField);
		// Just a dummy widget for focus-change
		add(new JButton("Dummy"));

		textField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				aText = textField.getText();
				textField.setFont(textField.getFont().deriveFont(Font.PLAIN));
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});

		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateFont();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateFont();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateFont();
			}

			private void updateFont() {
				if (aText == null || !aText.equals(textField.getText())) {
					textField
							.setFont(textField.getFont().deriveFont(Font.BOLD));
				} else {
					textField.setFont(textField.getFont()
							.deriveFont(Font.PLAIN));
				}
			}
		});
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// UIManager.setLookAndFeel(new WindowsLookAndFeel());
					// UIManager.setLookAndFeel(new MetalLookAndFeel());
					UIManager
							.setLookAndFeel(new SubstanceRavenGraphiteLookAndFeel());
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}

				JFrame frame = new JFrame();
				Issue404 contentPane = new Issue404();
				frame.setContentPane(contentPane);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
