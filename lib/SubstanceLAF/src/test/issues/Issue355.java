package test.issues;

import java.awt.FlowLayout;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class Issue355 extends JFrame {
	public class MousePopupListener extends MouseAdapter {
		/**
		 * The owner component.
		 */
		private JComponent owner;

		/**
		 * Creates a popup listener.
		 * 
		 * @param owner
		 *            The owner component.
		 */
		public MousePopupListener(JComponent owner) {
			this.owner = owner;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			System.out.println(System.currentTimeMillis()
					+ " - mouse pressed");
			checkPopup(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
			System.out.println(System.currentTimeMillis()
					+ " - mouse clicked");
			checkPopup(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
			System.out.println(System.currentTimeMillis()
					+ " - mouse released");
			checkPopup(e);
		}

		/**
		 * Handles the mouse event, showing the popup menu as necessary.
		 * 
		 * @param e
		 *            Mouse event.
		 */
		void checkPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				System.out.println(System.currentTimeMillis()
						+ " - popup trigger detected");

				JPopupMenu popup = new JPopupMenu();
				for (int i = 0; i < 10; i++)
					popup.add(new JMenuItem("Entry " + i));
				popup.addPopupMenuListener(new PopupPrintListener());

				popup.show(this.owner, e.getX(), e.getY());
				popup.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentShown(ComponentEvent e) {
						System.out.println(System.currentTimeMillis()
								+ " - popup shown");
					}
				});
			}
		}

	}

	/**
	 * Custom popup listener.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class PopupPrintListener implements PopupMenuListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent)
		 */
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			System.out.println(System.currentTimeMillis()
					+ " - popup menu will be visible!");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent)
		 */
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.event.PopupMenuListener#popupMenuCanceled(javax.swing.event.PopupMenuEvent)
		 */
		public void popupMenuCanceled(PopupMenuEvent e) {
		}
	}
	
	public Issue355() {
		super("Check popup menu latency");
		JButton button = new JButton("Right-click me!");
		this.setLayout(new FlowLayout());
		this.add(button);
		
		button.addMouseListener(new MousePopupListener(button));
		this.setSize(200, 100);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		//PopupFactory.setSharedInstance(new PopupFactory());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Issue355().setVisible(true);
			}
		});
	}

}
