package test.contrib;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceConstants.SubstanceWidgetType;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

@SuppressWarnings("serial")
public class BasicLayouts extends JFrame {
	public BasicLayouts() {
		super("Basic layouts");
		// create components of window
		createMenuBar();
		Container horizontalLayout = createHorizontalGroupBox();
		Container gridLayout = createGridLayout();
		Container formLayout = createFormLayout();
		Container textArea = createTextArea();
		Container dialogButtons = createDialogButtons();
		// set group layout to content pane
		Container contentPane = getContentPane();
		GroupLayout mainLayout = new GroupLayout(contentPane);
		contentPane.setLayout(mainLayout);
		mainLayout.setAutoCreateContainerGaps(true);
		mainLayout.setAutoCreateGaps(true);
		// add components of window to group layout
		mainLayout.setHorizontalGroup(mainLayout.createParallelGroup()
				.addComponent(horizontalLayout).addComponent(gridLayout)
				.addComponent(formLayout).addComponent(textArea).addComponent(
						dialogButtons));
		mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
				.addComponent(horizontalLayout).addComponent(gridLayout)
				.addComponent(formLayout).addComponent(textArea).addComponent(
						dialogButtons));
		//
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		SubstanceLookAndFeel.setWidgetVisible(getRootPane(), true,
				SubstanceWidgetType.TITLE_PANE_HEAP_STATUS);

		pack();
		setMinimumSize(getSize());
		setVisible(true);
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem exitItem = new JMenuItem(new AbstractAction() {
			{
				putValue(NAME, "Exit");
				putValue(MNEMONIC_KEY, KeyEvent.VK_X);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
	}

	private Container createHorizontalGroupBox() {
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(panel);
		groupLayout.setAutoCreateGaps(true);
		final int nButtons = 4;
		SequentialGroup horizontalGroup = groupLayout.createSequentialGroup();
		ParallelGroup verticalGroup = groupLayout.createParallelGroup();
		for (int i = 1; i <= nButtons; i++) {
			JButton button = new JButton("Button " + i);
			horizontalGroup.addComponent(button, GroupLayout.DEFAULT_SIZE,
					GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
			verticalGroup.addComponent(button);
		}
		groupLayout.setHorizontalGroup(horizontalGroup);
		groupLayout.setVerticalGroup(verticalGroup);
		panel.setLayout(groupLayout);
		Insets insets = new Insets(2, 2, 2, 2);
		panel
				.setBorder(new CompoundBorder(new TitledBorder(
						"Horizontal layout"), new EmptyBorder(insets)));
		return panel;
	}

	private Container createGridLayout() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JLabel label1st = new JLabel("Line 1:");
		JLabel label2nd = new JLabel("Line 2:");
		JLabel label3rd = new JLabel("Line 3:");
		JTextField textField1st = new JTextField();
		JTextField textField2nd = new JTextField();
		JTextField textField3rd = new JTextField();
		JTextArea textArea = new JTextArea(
				"This widget takes up about two thirds of the grid layout.");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		Insets insets = new Insets(2, 2, 2, 2);
		panel.setBorder(new CompoundBorder(new TitledBorder("Grid layout"),
				new EmptyBorder(insets)));
		Component glue = Box.createGlue();
		panel.add(glue, new GridBagConstraints(0, 0, 2, 1, 0.5, 1.0, CENTER,
				BOTH, insets, 0, 0));
		panel.add(label1st, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				CENTER, NONE, insets, 0, 0));
		panel.add(label2nd, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				CENTER, NONE, insets, 0, 0));
		panel.add(label3rd, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				CENTER, NONE, insets, 0, 0));
		panel.add(textField1st, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.0,
				CENTER, HORIZONTAL, insets, 0, 0));
		panel.add(textField2nd, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.0,
				CENTER, HORIZONTAL, insets, 0, 0));
		panel.add(textField3rd, new GridBagConstraints(1, 3, 1, 1, 0.5, 0.0,
				CENTER, HORIZONTAL, insets, 0, 0));
		panel.add(scrollPane, new GridBagConstraints(2, 0, 1, 4, 1.0, 1.0,
				CENTER, BOTH, insets, 0, 0));
		Dimension zeroSize = new Dimension(0,
				textField1st.getPreferredSize().height);
		textField1st.setPreferredSize(zeroSize);
		textField2nd.setPreferredSize(zeroSize);
		textField3rd.setPreferredSize(zeroSize);
		scrollPane.setPreferredSize(zeroSize);
		return panel;
	}

	private Container createFormLayout() {
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(panel);
		groupLayout.setAutoCreateGaps(true);
		JLabel label1st = new JLabel("Line 1:");
		JLabel label2nd = new JLabel("Line 2, long text:");
		JLabel label3rd = new JLabel("Line 3:");
		JTextField textField = new JTextField();
		JComboBox comboBox = new JComboBox();
		JSpinner spinner = new JSpinner();
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(
						groupLayout.createParallelGroup()
								.addComponent(label1st).addComponent(label2nd)
								.addComponent(label3rd)).addGroup(
						groupLayout.createParallelGroup().addComponent(
								textField).addComponent(comboBox).addComponent(
								spinner)));
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(
						groupLayout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(
								label1st).addComponent(textField)).addGroup(
						groupLayout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(
								label2nd).addComponent(comboBox)).addGroup(
						groupLayout.createParallelGroup(
								GroupLayout.Alignment.BASELINE).addComponent(
								label3rd).addComponent(spinner)));
		panel.setLayout(groupLayout);
		Insets insets = new Insets(2, 2, 2, 2);
		panel.setBorder(new CompoundBorder(new TitledBorder("Form layout"),
				new EmptyBorder(insets)));
		return panel;
	}

	private Container createTextArea() {
		JTextArea textArea = new JTextArea(
				"This widget takes up all the remaining space "
						+ "in the top-level layout.");
		textArea.setLineWrap(true);
		textArea.setRows(4);
		return new JScrollPane(textArea);
	}

	private Container createDialogButtons() {
		JPanel panel = new JPanel();
		JButton ok = new JButton("OK");
		JButton cancel = new JButton("Cancel");
		GroupLayout groupLayout = new GroupLayout(panel);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGap(0, 0, Short.MAX_VALUE).addComponent(ok).addComponent(
						cancel));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup()
				.addComponent(ok).addComponent(cancel));
		groupLayout.linkSize(ok, cancel);
		panel.setLayout(groupLayout);
		return panel;
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager
					.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new BasicLayouts();
			}
		});
	}
}
