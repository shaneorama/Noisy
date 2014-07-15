package test;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ScreenInsetsTest extends JFrame {

	public ScreenInsetsTest() {
		Toolkit.getDefaultToolkit().addPropertyChangeListener("win.propNames",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						System.out.println(Toolkit.getDefaultToolkit()
								.getScreenInsets(
										GraphicsEnvironment
												.getLocalGraphicsEnvironment()
												.getDefaultScreenDevice()
												.getDefaultConfiguration()));
					}
				});

		this.addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				System.out.println(e.getID() + ":" + e.getOldState() + ":"
						+ e.getNewState());
			}
		});

		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ScreenInsetsTest().setVisible(true);
			}
		});
	}
}
