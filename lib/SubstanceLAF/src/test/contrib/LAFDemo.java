package test.contrib;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.substance.SubstanceLookAndFeel;

public class LAFDemo extends JFrame {

    private JTextField textfield;
    private JCheckBox checkbox;

    public LAFDemo() {
        super("LAF-Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPasswordField password = new JPasswordField(10);
        textfield = new JTextField("Hallo, iX", 10);
        textfield.putClientProperty(LafWidget.TEXT_EDIT_CONTEXT_MENU,
                Boolean.TRUE);
        textfield.putClientProperty(LafWidget.TEXT_SELECT_ON_FOCUS,
                Boolean.TRUE);
        textfield.putClientProperty(LafWidget.TEXT_FLIP_SELECT_ON_ESCAPE,
                Boolean.TRUE);
        checkbox = new JCheckBox("editierbar");
        checkbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTextfield();
            }
        });
        updateTextfield();
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));
        p.add(password);
        p.add(checkbox);
        p.add(textfield);
        setContentPane(p);
        pack();
    }

    private void updateTextfield() {
        boolean selected = checkbox.isSelected();
        textfield.setEditable(selected);
        if (selected) {
            textfield.requestFocus();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                try {
                    UIManager
                            .setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel");
                } catch (Throwable thr) {
                    thr.printStackTrace();
                }
                UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS, Boolean.TRUE);
                new LAFDemo().setVisible(true);
            }
        });
    }
}
