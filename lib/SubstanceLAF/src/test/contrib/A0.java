package test.contrib;
import java.awt.event.*;
import javax.swing.JButton;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.*;
import org.jvnet.substance.skin.MistSilverSkin;

public class A0 extends javax.swing.JFrame {

    /** Creates new form A0 */
    public A0() {
        initMyCmpnts();
    }

    /** This method is called from within the constructor to
    * initialize the form.
    */
    private void initMyCmpnts() {
        setTitle("Substance LAF Test");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        add(jBtn1).setBounds(20, 20, 150, 25);
        add(jBtn2).setBounds(190, 20, 150, 25);
        jBtn2.setEnabled(false);
        jBtn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SubstanceLookAndFeel.setSkin(new MistSilverSkin());
                jBtn2.setEnabled(true);
            }
        });
        jBtn2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
// "I don't know in this part..."
                SubstanceSkin newSkin =
SubstanceLookAndFeel.getCurrentSkin().transform(
                    new ColorSchemeTransform() {
                        @Override
                        public SubstanceColorScheme
transform(SubstanceColorScheme scheme) {
                            return scheme.hueShift(0.1).saturate(0.5);
                        };
                    }, "hueShifted Skin?");
                SubstanceLookAndFeel.setSkin(newSkin);
// "Please help..."
            }
        });
        pack();
        setSize(380, 100);
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel
                (javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new A0().setVisible(true);
            }
        });
    }

    // Variables declaration
    private JButton jBtn1 = new JButton("MistSilverSkin");
    private JButton jBtn2 = new JButton("hueShift 10%");
    // End of variables declaration

}