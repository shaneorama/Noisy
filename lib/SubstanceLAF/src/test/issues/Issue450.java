package test.issues;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessSkin;

public class Issue450 extends JFrame {
	public Issue450() {
		super("Issue 450");
		final JTable table = new JTable(10, 4);
		this.add(new JScrollPane(table));

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		JButton tableRed = new JButton("table red");
		tableRed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setBackground(Color.red);
			}
		});
		JButton tableGreen = new JButton("table green");
		tableGreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setBackground(Color.green);
			}
		});
		JButton tableRevert = new JButton("table revert");
		tableRevert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setBackground(UIManager.getColor("Table.background"));
			}
		});
		JButton tableHeaderBlue = new JButton("header blue");
		tableHeaderBlue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.getTableHeader().setBackground(Color.blue);
			}
		});
		JButton tableHeaderYellow = new JButton("header yellow");
		tableHeaderYellow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.getTableHeader().setBackground(Color.yellow);
			}
		});
		controls.add(tableRed);
		controls.add(tableGreen);
		controls.add(tableRevert);
		controls.add(tableHeaderBlue);
		controls.add(tableHeaderYellow);

		this.add(controls, BorderLayout.SOUTH);

		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SubstanceLookAndFeel.setSkin(new BusinessSkin());
				new Issue450().setVisible(true);
			}
		});
	}

}
