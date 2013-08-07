package com.joshlong.markup.asciidoc;

import java.io.*;

/**
 * Strings <em>are</em> basically all we do in parsers. Geez!
 *
 * @author Josh Long
 */
public class StringUtils {
	public static void write(Writer writer, String toWrite) {
		try {
			writer.write(toWrite);
			writer.flush();
		}
		catch (IOException e) {
			throw new RuntimeException("couldn't write the string to the writer.");
		}
		finally {
			try {
				writer.close();
			}
			catch (IOException e) {
				// don't care
			}
		}
	}

	public static void write(File file, String content) {
		try {
			FileWriter fileWriter = new FileWriter(file);
			write(fileWriter, content);
		}
		catch (IOException e) {
			throw new RuntimeException("couldn't write to the file " + file.getAbsolutePath());
		}
	}

	public static String read(File file) {
		try {
			FileReader fileReader = new FileReader(file);
			return read(fileReader);
		}
		catch (FileNotFoundException e) {
			throw new RuntimeException("couldn't read the file " + file.getAbsolutePath());
		}

	}

	public static String defaultString(String a, String b) {
		return a != null && a.length() > 0 ? a : b;
	}

	public static String read(Reader is) {
		BufferedReader bufferedReader = new BufferedReader(is);
		StringBuilder string = new StringBuilder();
		try {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				string.append(line);
			}
			string.trimToSize();
			return string.toString();
		}
		catch (IOException e) {
			throw new RuntimeException("couldn't read from reader.");
		}
		finally {
			try {
				bufferedReader.close();
			}
			catch (IOException e) {
				// don't care
			}
		}
	}
}
