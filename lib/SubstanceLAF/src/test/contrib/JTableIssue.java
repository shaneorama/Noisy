package test.contrib;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class JTableIssue extends JFrame {

	private final Object[][] testData = new Object[0][2];
	private final String[] columns = { "Column 1", "Column 2" };

	private JButton btnChange = new JButton("Change");
	private JTable tblData = new JTable(testData, columns) {
		@Override
		@SuppressWarnings("unchecked")
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
	};
	private JScrollPane scrpnData = new JScrollPane(tblData);

	private JTableIssue() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new FlowLayout(FlowLayout.CENTER));

		btnChange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(tblData.getColumnClass(0));
			}
		});

		add(scrpnData);
		add(btnChange);

		pack();
	}

	private void refreshTable() {
		Object[][] testData2 = { { "1", Boolean.TRUE }, { "2", Boolean.FALSE } };
		JTable tblData2 = new JTable(testData2, columns) {
			@Override
			@SuppressWarnings("unchecked")
			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}
		};

		tblData.setModel(tblData2.getModel());
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JTableIssue().setVisible(true);
			}
		});
	}
}