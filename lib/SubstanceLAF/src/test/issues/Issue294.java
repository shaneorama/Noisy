package test.issues;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Component;
import java.util.Vector;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel;

/**
 * Simple non-editable combo under substance lnf.
 * 
 * @author Prashant Bhat
 */
public class Issue294 {

	/**
	 * @param args
	 * 		the command line arguments
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				try {
					UIManager
							.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
				} catch (Exception e) {
					e.printStackTrace();
				}
				JFrame frame = new JFrame("Non-editable combo with renderer");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Issue294 substanceCombo2 = new Issue294();
				frame.add(substanceCombo2.createControl());
				frame.pack();
				frame.setSize(250, 150);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	private JComponent createControl() {
		JComboBox jComboBox = new JComboBox(createCustomersList());
		jComboBox.setRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Component comp = super.getListCellRendererComponent(list,
						value, index, isSelected, cellHasFocus);
				setText(((Customer) value).getName());
				((JComponent) comp).setOpaque(false);
				return comp;
			}
		});
		JButton jButton1 = new JButton("Ok");
		JButton jButton2 = new JButton("Close");
		JPanel controlPanel = new JPanel();
		GroupLayout layout = new GroupLayout(controlPanel);
		controlPanel.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addGroup(
												layout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				jButton1)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jButton2))
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGap(
																				38,
																				38,
																				38)
																		.addComponent(
																				jComboBox,
																				GroupLayout.PREFERRED_SIZE,
																				177,
																				GroupLayout.PREFERRED_SIZE)))
										.addContainerGap(
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(
						jComboBox, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(31, 31, 31).addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
										.addComponent(jButton1).addComponent(
												jButton2)).addContainerGap(
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		return controlPanel;
	}

	private Vector<Customer> createCustomersList() {
		Vector<Customer> customers = new Vector<Customer>();
		customers.add(new Customer(1, "Bryan Chernick", "Regina"));
		customers.add(new Customer(2, "Clayton Lawless", "Maryfield"));
		customers.add(new Customer(3, "Linus Torvalds", "Portland"));
		customers.add(new Customer(4, "David Wilson", "Regina"));
		customers.add(new Customer(5, "King, Stephen", "Portland"));
		customers.add(new Customer(6, "Dan Bonin", "Milton"));
		customers.add(new Customer(7, "Doris Wilson", "Regina"));
		customers.add(new Customer(8, "Dyana Wilson", "Calgary"));
		customers.add(new Customer(9, "Graham Cale", "Walkerton"));
		customers.add(new Customer(10, "Jennifer Wilson", "Victoria"));
		customers.add(new Customer(11, "Matt Groening", "Portland"));
		customers.add(new Customer(12, "Jesse Wilson", "Waterloo"));
		customers.add(new Customer(13, "Joe Murray", "Cambridge"));
		customers.add(new Customer(14, "Jodie Ashford", "Caledonia"));
		customers.add(new Customer(15, "Jono Feldstein", "Toronto"));
		customers.add(new Customer(16, "Kevin Maltby", "Waterloo"));
		customers.add(new Customer(17, "Leanne Wilson", "Calgary"));
		customers.add(new Customer(18, "Lisa Kent", "Cambridge"));
		customers.add(new Customer(19, "Naomi Williams", "Guelph"));
		customers.add(new Customer(20, "Mark Wilson", "Assiniboia"));
		customers.add(new Customer(21, "Chuck Palahniuk", "Portland"));
		customers.add(new Customer(22, "Phil O'Dell", "Cambridge"));
		customers.add(new Customer(23, "Courtney Love", "Portland"));
		customers.add(new Customer(24, "Robbie Anderson", "Regina"));
		customers.add(new Customer(25, "Tuong Mao", "Regina"));
		customers.add(new Customer(26, "Wilson Harron", "Guelph"));
		customers.add(new Customer(27, "Sebastian Telfair", "Portland"));
		customers.add(new Customer(28, "Katherine Dunn", "Portland"));
		return customers;
	}

	public class Customer implements Comparable<Customer> {

		public int code;
		public String name;
		public String location;

		public Customer(int code, String name, String location) {
			this.code = code;
			this.name = name;
			this.location = location;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public int compareTo(Customer o) {
			return name.compareTo(o.name);
		}
	}
}
