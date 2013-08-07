package com.joshlong.markup.asciidoc;

import java.io.File;
import java.util.*;

import static com.joshlong.markup.asciidoc.StringUtils.*;

/**
 * Expects one or two arguments on the command line: {@code -in}, followed by a valid path, and {@code -out}, also
 * followed by a valid path. <EM>Valid</EM> paths are paths that may be passed to {@link File#File(String)}.
 *
 * @author Josh Long
 */
public class Main {
	public static void main(String[] args) throws Throwable {
		final String in = "in", out = "out";

		Map<String, String> options = new HashMap<String, String>();
		int index = 0;
		for (String arg : args) {
			if (arg.equals("-" + in)){
				options.put(in, args[index + 1]);
			}
			if (arg.equals("-" + out)){
				options.put(out, args[index + 1]);
			}
			index += 1;
		}

		AsciidocPegDownProcessor pegDownProcessor = new AsciidocPegDownProcessor();
		String userHome = System.getProperty("user.home");
		File inFile = new File(defaultString(options.get(in), userHome + "/" + in + ".md"));
		File outFile = new File(defaultString(options.get(out), userHome + "/" + out + ".asc"));
		String md = read(inFile);
		String asciidoc = pegDownProcessor.markdownToAsciidoc(md);
		write(outFile, asciidoc);
	}
}
