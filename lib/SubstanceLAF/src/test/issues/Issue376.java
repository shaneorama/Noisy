package test.issues;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel;

public class Issue376 extends JDialog {

	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JList list = null;

	private final static ImageIcon LOADING_ICON = new ImageIcon(Issue376.class
			.getResource("/test/issues/button_yellow.gif"));

	/**
	 * @param owner
	 */
	public Issue376() {
		setPreferredSize(new java.awt.Dimension(100, 150));
		setContentPane(getJContentPane());
		this.setLocationRelativeTo(null);

	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getScrollPane(), BorderLayout.CENTER);
			jContentPane.add(addImage(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	private JButton addImage() {
		JButton add = new JButton("Add");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Adding new element at index "
						+ getList().getModel().getSize());
				((DefaultListModel) getList().getModel())
						.addElement(LOADING_ICON);
			}
		});

		return add;
	}

	private JScrollPane getScrollPane() {
		if (jScrollPane == null)
			jScrollPane = new JScrollPane(getList());

		return jScrollPane;
	}

	private JList getList() {
		if (list == null) {
			DefaultListModel model = new DefaultListModel();
			list = new JList(model) {
				@Override
				public void paint(java.awt.Graphics g) {

					System.out.println("In JList.paint(): visible index0="
							+ getFirstVisibleIndex() + ", index1="
							+ getLastVisibleIndex());

					super.paint(g);
				}
			};

			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			list.setVisibleRowCount(1);
		}

		return list;
	}

	public static void main(String[] argv) throws Exception {
		UIManager.setLookAndFeel(new SubstanceRavenGraphiteGlassLookAndFeel());
		Issue376 dlg = new Issue376();
		dlg.pack();
		dlg.setVisible(true);
	}

}
