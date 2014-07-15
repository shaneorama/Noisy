package test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.lafwidget.utils.LafConstants.AnimationKind;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessSkin;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

public class TestPlaceholder extends JFrame {
	public TestPlaceholder() {
		super("Some simple text");


		this.setLayout(new BorderLayout());
		JDesktopPane jdp = new JDesktopPane();
		this.add(jdp, BorderLayout.CENTER);
		final JInternalFrame jif = new JInternalFrame("Test");
		jif.setClosable(true);
		jif.setMaximizable(true);
		jif.setIconifiable(true);
		jif.setVisible(true);
		jif.setBounds(20, 20, 300, 200);
		jdp.add(jif);
		
		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton changeSkin = new JButton("change skin");
		changeSkin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SubstanceLookAndFeel.setSkin(new BusinessSkin());
			}
		});
		controls.add(changeSkin);
		JButton isClosable = new JButton("is closable?");
		isClosable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(jif.isClosable());
			}
		});
		controls.add(isClosable);
		this.add(controls, BorderLayout.SOUTH);

		this.setSize(650, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws Exception {
		JFrame.setDefaultLookAndFeelDecorated(true);
		//System.out.println(UIManager.get("MenuBarUI"));
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TestPlaceholder().setVisible(true);
			}
		});
	}
}
