package test.contrib;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessSkin;

public class Main extends JFrame {

	public static void main(String[] args) throws Throwable {
		setLookAndFeel();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Main().setVisible(true);
			}
		});
	}

	private static void setLookAndFeel() throws Throwable {
		//UIManager.setLookAndFeel(new MyLookAndFeel());
		SubstanceLookAndFeel.setSkin(new BusinessSkin());
		UIManager.put("TextField.inactiveBackground", Color.green);
		UIManager.put("TextField.disabledBackground", Color.green);
		// UIManager.put("TextField.background", Color.white);
	}

	private JTextField field01;
	private JTextField field02;
	private JButton button01;

	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 800, 600);

		field01 = new JTextField("Click to change \"Editable\" property");
		field02 = new JTextField("Click to change \"Enabled\" property");

		button01 = new JButton("Switch to NOT editable/enabled");
		button01.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				field01.setEditable(!field01.isEditable());
				field02.setEnabled(!field02.isEnabled());
			}

		});

		Container pane = getContentPane();
		pane.setLayout(new FlowLayout());
		pane.add(field01);
		pane.add(field02);
		pane.add(button01);
	}

}
