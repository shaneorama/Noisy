package test.issues;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JDialog;
import javax.swing.UIManager;
import org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel;

class Issue369 extends JDialog
{
    private final static String[] columnsNames = new String[]{ "", "header 2"};
    private final static String [][] values = new String[][]{ {"1-01", "1-02"},
{"2-01", "2-02"}, {"3-01", "3-02"}, {"4-01", "4-02"} };

    Issue369()
    {
    	setLayout( new BorderLayout());
    	add( new JScrollPane( new JTable( values, columnsNames)), BorderLayout.NORTH);
    	setSize( 250,250);
    }
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
    	UIManager.setLookAndFeel( new SubstanceRavenGraphiteGlassLookAndFeel());
		Issue369 dlg = new Issue369();
		dlg.setVisible(true);
		
    }
 
}