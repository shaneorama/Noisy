package test.contrib;

import javax.swing.*;

public class InternalFrameDemo extends JFrame {

	private static final long serialVersionUID = 1L;

	private JDesktopPane pane;

	public InternalFrameDemo() {
		pane = new JDesktopPane();
		pane.add(createFrame("Test", 200, 150));
		pane.add(createFrame("Test 2", 300, 200));
		setContentPane(pane);
	}

	private JInternalFrame createFrame(String title, int width, int height) {
		JInternalFrame f = new JInternalFrame(title, true, true, true, true);
		f.add(new JButton("Test"));
		f.setLocation(0, 0);
		f.setSize(width, height);
		f.setVisible(true);
		f.addComponentListener(new SnapListener(pane));
		return f;
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// try {
				// UIManager
				// .setLookAndFeel(new SubstanceBusinessLookAndFeel());
				// } catch (UnsupportedLookAndFeelException e) {
				// e.printStackTrace();
				// }
				InternalFrameDemo demo = new InternalFrameDemo();
				demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				demo.setSize(800, 600);
				demo.setVisible(true);
			}
		});
	}
}
