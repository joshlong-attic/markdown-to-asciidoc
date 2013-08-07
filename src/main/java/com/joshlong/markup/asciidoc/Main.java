/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joshlong.markup.asciidoc;

import java.io.*;
import java.util.*;

import static com.joshlong.markup.asciidoc.StringUtils.*;

/**
 * Expects one or two arguments on the command line: {@code -IN}, followed by a valid path, and {@code -OUT}, also
 * followed by a valid path. <EM>Valid</EM> paths are paths that may be passed to {@link File#File(String)}.
 *
 * @author Josh Long
 */
public class Main {

	public final static String USER_HOME = System.getProperty("user.home");
	public final static String IN = "in";
	public final static String OUT = "out";

	public static void main(String[] args) throws Throwable {
		AsciidocPegDownProcessor pegDownProcessor = new AsciidocPegDownProcessor();
		Map<String, String> argsMap = options(args);
		String markdown = read(inReader(argsMap));
		String asciidoc = pegDownProcessor.markdownToAsciidoc(markdown);
		write(outWriter(argsMap), asciidoc);
	}

	private static Reader inReader(Map<String, String> options) {
		return new InputStreamReader(in(options));
	}

	/** Returns an {@link InputStream} representing the file given as the {@code -in} argument, or standard input. */
	private static InputStream in(Map<String, String> options) {
		if (options.size() > 0){
			String inPath = defaultString(options.get(IN), new File(USER_HOME, IN + ".md").getAbsolutePath());
			try {
				return new FileInputStream(new File(inPath));
			}
			catch (FileNotFoundException e) {
				throw new RuntimeException("couldn't find the input stream file, " + inPath + ".", e);
			}
		}
		else {
			return System.in;
		}
	}

	private static Writer outWriter(Map<String, String> args) {
		return new OutputStreamWriter(out(args));
	}

	/** Returns an {@link OutputStream} representing the file given as the {@code -out} argument, or standard output. */
	private static OutputStream out(Map<String, String> args) {
		if (args.size() > 0){
			String outPath = defaultString(args.get(OUT), new File(USER_HOME, OUT + ".asc").getAbsolutePath());
			try {
				return new FileOutputStream(new File(outPath));
			}
			catch (FileNotFoundException e) {
				throw new RuntimeException("couldn't find the output stream file, " + outPath + ".", e);
			}
		}
		else {
			return System.out;
		}
	}

	private static Map<String, String> options(String[] args) {
		Map<String, String> options = new HashMap<String, String>();
		if (args != null && args.length > 0){
			int index = 0;
			for (String arg : args) {
				if (arg.equals("-" + IN)){
					options.put(IN, args[index + 1]);
				}
				if (arg.equals("-" + OUT)){
					options.put(OUT, args[index + 1]);
				}
				index += 1;
			}
		}
		return options;
	}
}
