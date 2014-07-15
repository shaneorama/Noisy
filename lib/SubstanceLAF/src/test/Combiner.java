package test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.utils.SubstanceColorSchemeUtilities;

public class Combiner {

	public static void main(String[] args) throws Exception {
		File inputs = new File(
				"C:\\jprojects\\substance\\src\\org\\jvnet\\substance\\skin");
		List<SubstanceColorScheme> schemes = new ArrayList<SubstanceColorScheme>();
		for (File input : inputs.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("office-")
						&& name.endsWith(".colorscheme");
			}
		})) {
			System.err.println("Processing " + input.getName());
			try {
				SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
						.getColorScheme(input.toURI().toURL());
				schemes.add(scheme);
			} catch (IllegalArgumentException iae) {
				System.err.println("Skipped");
			}
		}
		File output = new File(
				"C:\\jprojects\\substance\\src\\org\\jvnet\\substance\\skin\\office2007.colorschemes");
		PrintStream printStream = new PrintStream(new FileOutputStream(output));
		for (int i = 0; i < schemes.size(); i++) {
			SubstanceColorScheme colorScheme = schemes.get(i);
			String encodedColorScheme = colorScheme.toString();
			printStream.println(encodedColorScheme);
			printStream.println();
		}
		printStream.close();
	}

}
