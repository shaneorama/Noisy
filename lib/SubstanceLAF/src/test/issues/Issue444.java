package test.issues;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.UIManager;

/**
 * to see the error click in the frame
 */
public class Issue444 extends JFrame {

	public Issue444() {
		setSize(1050, 600);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		
		addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// the order of these statements or using invokeLater does not change anything
				setExtendedState(MAXIMIZED_BOTH);
				getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		        
			}
			
		});
		
		setVisible(true);
	}
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessLookAndFeel");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new Issue444();
            }
		});
		
	}
}
