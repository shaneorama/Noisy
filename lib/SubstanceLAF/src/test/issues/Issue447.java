package test.issues;

import javax.swing.*;

import org.jvnet.substance.skin.SubstanceBusinessLookAndFeel;

public class Issue447 extends JFrame {
	public Issue447() {
		super("Issue 447");
		this.setSize(300, 200);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager
							.setLookAndFeel(new SubstanceBusinessLookAndFeel());
				} catch (Throwable t) {
					t.printStackTrace(System.out);
				}
				System.out.println("LookAndFeel class : "
						+ UIManager.getLookAndFeel().getClass().getName());
				System.out.println("LookAndFeel name : "
						+ UIManager.getLookAndFeel().getName());

				try {
					UIDefaults defaults = UIManager.getDefaults();
					Object ui = defaults.get("PanelUI");
					System.out.println("PanelUI : " + ui);
					Object cl = defaults.get("ClassLoader");
					System.out.println("ClassLoader : " + cl);
					ClassLoader loader = (cl != null) ? (ClassLoader) cl
							: JPanel.class.getClassLoader();
					System.out.println("ClassLoader : " + loader);
					String uiClassName = (String) ui;
					System.out.println("UIClassName : " + uiClassName);

					Class cls = (Class) defaults.get(uiClassName);
					System.out.println("Cached class : " + cls);
					if (cls == null) {
						if (loader == null) {
							System.out.println("Using system loader to load "
									+ uiClassName);
							cls = Class.forName(uiClassName, true, Thread
									.currentThread().getContextClassLoader());
							System.out.println("Done loading");
						} else {
							System.out.println("Using custom loader to load "
									+ uiClassName);
							cls = loader.loadClass(uiClassName);
							System.out.println("Done loading");
						}
						if (cls != null) {
							System.out.println("Loaded class : "
									+ cls.getName());
						} else {
							System.out.println("Couldn't load the class");
						}
					}
				} catch (Throwable t) {
					t.printStackTrace(System.out);
				}

				new Issue447().setVisible(true);
			}
		});
	}

}
