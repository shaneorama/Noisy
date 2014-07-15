package test.issues;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Font;

public class Issue420 extends JFrame
{
	public static void main(String[] args) {
		SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run() {
				new Issue420().setVisible(true);
			}
		});
	}

	public Issue420() {
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setFont(progressBar.getFont().deriveFont(Font.BOLD, 32.0f));
		progressBar.setIndeterminate(false);
		progressBar.setStringPainted(true);
		progressBar.setString("some progressbar text");

		JPanel scrollViewport = new JPanel();
		scrollViewport.add(progressBar);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(scrollViewport);
		scrollPane.setPreferredSize(new Dimension(300, 65));

		getContentPane().add(scrollPane);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
