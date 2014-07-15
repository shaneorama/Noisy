package test.issues;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Issue362 extends JFrame {
	private static final long serialVersionUID = 1L;

	private int side = 0;
	JPanel leftPane, rightPane;
	JDesktopPane desktopPane;
	JSplitPane splitPane;

	public Issue362() {
		super();
	}

	private void initLAFTest2() {
		splitPane = new JSplitPane();
		leftPane = new JPanel(new BorderLayout());
		rightPane = new JPanel(new BorderLayout());

		splitPane.setLeftComponent(leftPane);
		splitPane.setRightComponent(rightPane);
		splitPane.setDividerLocation(500);

		desktopPane = new JDesktopPane();

		JInternalFrame doc1 = new JInternalFrame("Document 1", true, true,
				true, true);

		System.out.println("Done creating buttons.");

		doc1.setSize(300, 200);

		doc1.setLocation(10, 10);

		doc1.setVisible(true);

		desktopPane.setLayout(null);
		desktopPane.add(doc1);

		leftPane.add(desktopPane, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		JMenu menuTest = new JMenu("Test");
		JMenuItem menuItemSwap = new JMenuItem("Swap sides");
		menuItemSwap.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				side = 1 - side;
				System.out.println("Switching sides...");
				if (side == 0) {
					rightPane.remove(desktopPane);
					leftPane.add(desktopPane);
					splitPane.repaint();
				} else {
					leftPane.remove(desktopPane);
					rightPane.add(desktopPane);
					splitPane.repaint();
				}

			}
		});

		menuTest.add(menuItemSwap);
		menuBar.add(menuTest);

		this.setSize(1000, 500);
		this.setContentPane(splitPane);
		this.setJMenuBar(menuBar);
		this.setTitle("LaF test 2");
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					//
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());

					UIManager
							.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessLookAndFeel");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				Issue362 thisClass = new Issue362();
				thisClass.initLAFTest2();

				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}
}