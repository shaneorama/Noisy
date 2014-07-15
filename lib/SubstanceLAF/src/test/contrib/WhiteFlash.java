package test.contrib;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.RavenGraphiteGlassSkin;

public class WhiteFlash extends JFrame implements ActionListener
{
	JDialog dialog = null;
	JButton btn = null;

	public WhiteFlash()
	{
		init();
	}

	private void init()
	{
		this.setUndecorated(true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		setupDialog();
		btn = new JButton("Open");
		btn.addActionListener(this);
		JPanel holdPanel = new JPanel(new BorderLayout());
		holdPanel.add(btn, BorderLayout.SOUTH);
		getContentPane().add(holdPanel, java.awt.BorderLayout.CENTER);
		this.setSize(1024, 768);
		this.setResizable(false);
		this.setVisible(true);
	}
	private void setupDialog()
	{
		dialog = new JDialog(this, false);
		dialog.setTitle("TEST");
		dialog.setSize(150, 150);
		setResizable(false);
	}

	public static void main(String[] args)
	{
		SubstanceLookAndFeel.setSkin(new RavenGraphiteGlassSkin());
		UIManager.put(SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				WhiteFlash flash = new WhiteFlash();
				flash.setVisible(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == btn)
		{
			dialog.setVisible(true);
			dialog.toFront();
		}
	}
}
