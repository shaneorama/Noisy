package test.check;

import java.awt.*;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class AlignmentPanel extends JPanel implements Deferrable {
	private boolean isInitialized;

	@Override
	public boolean isInitialized() {
		return this.isInitialized;
	}

	public AlignmentPanel() {
		this.setLayout(new BorderLayout());
	}

	@Override
	public synchronized void initialize() {
		FormLayout lm = new FormLayout("left:pref:grow", "");
		DefaultFormBuilder builder = new DefaultFormBuilder(lm,
				new ScrollablePanel());
		builder.setDefaultDialogBorder();

		for (int fontSize = 11; fontSize < 25; fontSize++) {
			builder.append(getSubPanel(fontSize));
		}

		this.add(new JScrollPane(builder.getPanel()));
		this.isInitialized = true;
	}

	private static JPanel getSubPanel(int size) {
		JPanel result = new JPanel(new FlowLayout(FlowLayout.LEFT));

		Font font = new Font("Tahoma", Font.PLAIN, size);

		JLabel label = new JLabel("Tahoma " + size);
		label.setFont(font);
		result.add(label);

		JTextField tf = new JTextField("sample");
		tf.setFont(font);
		result.add(tf);

		// JFormattedTextField ftf = new JFormattedTextField("sample");
		// ftf.setFont(font);
		// result.add(ftf);

		JPasswordField pf = new JPasswordField("sample");
		pf.setFont(font);
		result.add(pf);

		JComboBox cb = new JComboBox(new Object[] { "sample" });
		cb.setFont(font);
		result.add(cb);

		JComboBox ecb = new JComboBox(new Object[] { "sample" });
		ecb.setFont(font);
		ecb.setEditable(true);
		result.add(ecb);

		JSpinner s = new JSpinner(new SpinnerListModel(new Object[] {
				"sample0", "sample", "sample2" }));
		s.getModel().setValue("sample");
		s.setFont(font);
		result.add(s);

		return result;
	}

	public static void main(String[] args) throws Exception {
		JFrame.setDefaultLookAndFeelDecorated(true);
		UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("Alignment");
				frame.setSize(600, 400);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				AlignmentPanel panel = new AlignmentPanel();
				panel.initialize();
				frame.add(panel, BorderLayout.CENTER);
				frame.setVisible(true);
			}
		});
	}
}
